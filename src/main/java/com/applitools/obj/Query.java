package com.applitools.obj;

import com.applitools.obj.Serialized.StoredBatchInfoId;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Query {
    //private static final String GET_STORED_BATCH_INFO_ID_TMPL = "https://%s/api/sessions/batches/batchId/%s?format=json&ApiKey=%s";
    private static final String GET_STORED_BATCH_INFO_ID_TMPL = "https://%s/api/sessions/batches/batchId/%s?format=json&userName=%s&userId=%s";
    private static final String QUERY_TMPL_REGEX = "^(?<op>\\w+):(?<phrase>[a-zA-Z0-9,_-]+,?)$";
    private static final Pattern QUERY_PTRN = Pattern.compile(QUERY_TMPL_REGEX);
    private static final ObjectMapper mapper = new ObjectMapper();

    private final QUERY_OPS operation_;
    //private final String viewKey_;
    private final String phrase_;
    private final Matcher matcher_;
    private String userName_;
    private String userId_;
    private final String serverUrl_;

    private enum QUERY_OPS {
        sid,
        //TODO More to come
    }

    public Query(String query, String serverUrl, String userName, String userId) {
        matcher_ = QUERY_PTRN.matcher(query);
        userName_ = userName;
        userId_ = userId;
        if (!matcher_.find())
            throw new RuntimeException(String.format("Invalid syntax, query: %s \n", query));
        serverUrl_ = serverUrl;
        //viewKey_ = viewKey;
        operation_ = QUERY_OPS.valueOf(matcher_.group("op"));
        phrase_ = matcher_.group("phrase");
    }

    public static boolean isMatching(String query) {
        return QUERY_PTRN.matcher(query).find();
    }

    public List<String> getBatchIds() throws IOException {
        switch (operation_) {
            case sid:
                return getBatchIdsBySids(phrase_.split(","));
            default:
                throw new RuntimeException(String.format("Invalid operation %s \n", matcher_.group("op")));
        }
    }

    private List<String> getBatchIdsBySids(String[] sids) throws IOException {
        ArrayList<String> ids = new ArrayList<>();
        for (String sid : sids) {
            String id = getIdBySid(sid);
            if (id == null || StringUtils.isEmpty(id)) continue;
            ids.add(id);
        }
        return ids;
    }

    private String getIdBySid(String sid) throws IOException {
        String getStoredBatchInfoId = String.format(GET_STORED_BATCH_INFO_ID_TMPL, serverUrl_, sid, userName_, userId_);
        StoredBatchInfoId storedBatchInfoId = mapper.readValue(new URL(getStoredBatchInfoId), StoredBatchInfoId.class);
        return storedBatchInfoId.getBatchId();
    }
}
