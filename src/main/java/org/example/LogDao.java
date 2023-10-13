package org.example;

import com.mongodb.client.result.DeleteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * @author hweig
 */
@Component
public class LogDao {

    private static final String TABLE_NAME = "device_log";

    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * @param gsn
     */
    public DeleteResult deleteLog(String gsn) {
        Criteria criteria = Criteria.where("sn").is(gsn);
        Query query = new Query(criteria);

        return mongoTemplate.remove(query, TABLE_NAME);
    }
}
