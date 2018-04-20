package com.applitools.Commands;

import com.applitools.obj.AdminApi;
import com.applitools.obj.Serialized.Admin.Account;
import com.applitools.obj.Serialized.Admin.Subscriber;
import com.applitools.obj.Serialized.Admin.User;
import com.applitools.utils.Utils;
import com.applitools.utils.Validate;
import com.beust.jcommander.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Parameters(commandDescription = "Manage users and teams on the server")
public class Admin implements Command {
    private static final String GETID = "getId";
    private static final String GETTEAMS = "getTeams";
    private static final String GETUSERS = "getUsers";
    private static final String ADDTEAM = "addTeam";
    private static final String ADDUSER = "addUser";
    private static final String REMUSER = "remUser";

    public void printHelp(String subcmd) {
        jc.usage(subcmd);
    }

    //region sub commadns
    @Parameters(commandDescription = "Get user-id by providing username and password")
    private abstract class AdminSubCommand implements Command {
        @Parameter(names = {"-as", "--server"}, description = "Applitools server url")
        protected String server = "eyes.applitools.com";
        @Parameter(names = {"-un", "--username"}, required = true)
        protected String user;
    }

    private abstract class AdminOrgSubCommand extends AdminSubCommand {
        @Parameter(names = {"-or", "--orgId"}, description = "Organization id as it appears in your urls", required = true)
        protected String orgId;
        @Parameter(names = {"-ui", "--userId"}, description = "User id as it was extracted from getId command", required = true)
        protected String userId;
    }

    @Parameters(commandDescription = "Exchange username and password to user-id")
    private class GetId extends AdminSubCommand {
        @Parameter(names = {"-up", "--password"}, required = true)
        private String password;

        public void run() throws Exception {
            String userId = AdminApi.getUserId(server, user, password);
            System.out.printf("%s\n", java.net.URLDecoder.decode(userId, "UTF-8"));
        }
    }

    @Parameters(commandDescription = "List all the teams in organization")
    private class GetTeams extends AdminOrgSubCommand {
        public void run() throws Exception {
            AdminApi api = new AdminApi(server, orgId, user, userId);
            Account[] accounts = api.getAccounts();
            for (Account account : accounts) {
                System.out.printf("%s\t|\t%s\n", account.getId(), account.getName());
            }
        }
    }

    @Parameters(commandDescription = "List all the users in a specific team in the organization")
    private class GetUsers extends AdminOrgSubCommand {
        private static final String TABS = "|%-35s|%-20s|%-35s|%-7s|%-8s|\n";
        @Parameter(names = {"-ti", "--teamId"}, description = "The team id", required = true)
        private String teamId;

        public void run() throws Exception {
            AdminApi api = new AdminApi(server, orgId, user, userId);
            Account account = api.getAccount(teamId);

            if (account == null)
                throw new RuntimeException("No team was found!");

            User[] users = api.getUsers();
            System.out.printf(TABS, "             Username", "        Name", "               Email", " Admin ", " Viewer ");
            for (Subscriber sub : account.getMembers().values()) {
                User currUser = api.getUserById(sub.getName());
                if (currUser == null) {
                    System.out.printf(TABS,
                            currUser.getId(),
                            currUser.getFullName(),
                            currUser.getEmail(),
                            sub.getIsAdmin() ? "   x  " : "",
                            sub.getIsViewer() ? "    x  " : "");
                }
            }
        }

        private Account find(Account[] accounts, String teamId) {
            for (Account account : accounts) {
                if (account.getId().compareTo(teamId) == 0) return account;
            }
            return null;
        }
    }

    @Parameters(commandDescription = "Add New Team")
    private class AddNewTeam extends AdminOrgSubCommand {
        @Parameter(names = {"-tn", "--teamName"}, description = "Add new team", required = true)
        private String teamName;

        public void run() throws Exception {
            AdminApi api = new AdminApi(server, orgId, user, userId);
            Account account = api.addAccount(new Account(teamName));
            System.out.printf("Team id:  %s\n", account.getId());
        }
    }

    @Parameters(commandDescription = "Add a new user to team")
    private class AddUser extends AdminOrgSubCommand {
        @Parameter(names = {"-ti", "--teamId"}, description = "Team id", required = true)
        private String teamId;
        @Parameter(names = {"-ne", "--newUserEmail"}, description = "User Email")
        private String newUserEmail;
        @Parameter(names = {"-ni", "--newUserId"}, description = "User id to add")
        private String newUserId;
        @Parameter(names = {"-nn", "--newUserName"}, description = "User's full name", variableArity = true)
        private List<String> newUserName;
        @Parameter(names = {"-ve", "--viewer"}, description = "Set permissions to viewer")
        private boolean isViewer = false;
        @Parameter(names = {"-ad", "admin"}, description = "Set permissions to team-admin")
        private boolean isAdmin = false;

        @Override
        public void run() throws Exception {
            AdminApi api = new AdminApi(server, orgId, user, userId);
            User[] users = api.getUsers();
            User currUser = null;
            if (newUserId != null)
                currUser = api.getUserById(userId);
            else if (newUserEmail != null)
                currUser = api.getUserByEmail(newUserEmail);

            if (currUser == null) {
                if (newUserEmail==null)
                    throw new RuntimeException("Email required!"); //New user require email field
                if (newUserId == null)
                    newUserId = newUserEmail;//Decucing userid from email
                if (newUserName == null) { //Deducing username from email
                    newUserName = new ArrayList<>();
                    String[] emailparts = newUserEmail.split("@");
                    String name = emailparts[0];
                    name = WordUtils.capitalize(name.replaceAll(".", " "));
                    newUserName.add(name);
                }

                StringBuilder nameBuilder = new StringBuilder();
                for (String name : newUserName)
                    nameBuilder.append(name).append(" ");
                currUser = new User(newUserId, newUserEmail, nameBuilder.toString());

                api.addUser(currUser); //Adding user to the org
            }


            Account account = api.getAccount(teamId);
            if (account == null)
                throw new RuntimeException("Team is was not found!");

            account.add(currUser, isViewer, isAdmin);

            System.out.printf("Done!\n");
        }
    }

    @Parameters(commandDescription = "Remove user")
    private class RemoveUser extends AdminOrgSubCommand {

        @Parameter(names = {"-ri", "-removeUserId"}, description = "User id to remove", required = true)
        private String removeUserId;

        @Parameter(names = {"-ti", "-teamId"}, description = "Team id to remove the user from")
        private String removeTeamId;

        @Override
        public void run() throws Exception {
            AdminApi api = new AdminApi(server, orgId, user, userId);
            if (removeTeamId != null) {
                Account account = api.getAccount(removeTeamId);
                if (account == null)
                    throw new RuntimeException("Team is not present");
                account.remove(removeUserId);
                System.out.printf("done!");
            } else {
                System.out.printf("You are about to remove the user entirely, are you sure you want to proceed? (Y\\N)\n");
                BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
                String answer = buffer.readLine();
                if (answer.toUpperCase().compareTo("Y") == 0 || answer.toUpperCase().compareTo("YES") == 0) {
                    api.removeUser(userId);
                    System.out.printf("done!");
                } else
                    System.out.printf("Skipped");
            }
        }
    }

    //endregion
    private JCommander jc = new JCommander();

    @Parameter(names = {GETID}, description = "Get user-id from the server", variableArity = true)
    private List<String> getid = null;

    @Parameter(names = {GETTEAMS}, description = "Get teams info from the server", variableArity = true)
    private List<String> getTeams = null;

    @Parameter(names = {GETUSERS}, description = "Get team's users from the server", variableArity = true)
    private List<String> getUsers = null;

    @Parameter(names = {ADDTEAM}, description = "Add new team to the server", variableArity = true)
    private List<String> addTeam = null;

    @Parameter(names = {ADDUSER}, description = "Add new user to a team", variableArity = true)
    private List<String> addUser = null;

    @Parameter(names = {REMUSER}, description = "Remove user from team or entirely", variableArity = true)
    private List<String> remUser = null;

    public Admin() {
        jc.setProgramName("Admin");
        jc.addCommand(GETID, new GetId());
        jc.addCommand(GETTEAMS, new GetTeams());
        jc.addCommand(ADDTEAM, new AddNewTeam());
        jc.addCommand(GETUSERS, new GetUsers());
        jc.addCommand(ADDUSER, new AddUser());
        jc.addCommand(REMUSER, new RemoveUser());
    }

    public void run() throws Exception {
        if (Validate.isAllNull(getid, getTeams, addTeam, getUsers, addUser, remUser))
            interactiveAdmin();
        else if (!Validate.isExactlyOneNotNull(getid, getTeams, addTeam, getUsers, addUser, remUser))
            return;//todo something
        else {
            List<String> subargs = null;
            if (getid != null) {
                subargs = getid;
                subargs.add(0, GETID);
            } else if (getTeams != null) {
                subargs = getTeams;
                subargs.add(0, GETTEAMS);
            } else if (addTeam != null) {
                subargs = addTeam;
                subargs.add(0, ADDTEAM);
            } else if (getUsers != null) {
                subargs = getUsers;
                subargs.add(0, GETUSERS);
            } else if (addUser != null) {
                subargs = addUser;
                subargs.add(0, ADDUSER);
            } else if (remUser != null) {
                subargs = remUser;
                subargs.add(0, REMUSER);
            }

            String[] sargs = (String[]) subargs.toArray(new String[subargs.size()]);
            try {
                jc.parse(sargs);
                Command command = (Command) jc.getCommands().get(jc.getParsedCommand()).getObjects().get(0);
                command.run();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                jc.usage(jc.getParsedCommand());
            }
        }
    }

    private void interactiveAdmin() {
        //todo
        //But now will print help
        throw new RuntimeException("No parameters were provided");
    }

}
