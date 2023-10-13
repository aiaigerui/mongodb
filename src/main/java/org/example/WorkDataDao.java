package org.example;

import com.mongodb.bulk.BulkWriteResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.BulkOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author hweig
 */
@Component
public class WorkDataDao {

    private static final String table = "device_workdata_inverter";

    @Autowired
    private MongoTemplate mongoTemplate;

    public BulkWriteResult updateExpireAt(String sn, Date date) {
        BulkOperations bulkOperations = mongoTemplate.bulkOps(BulkOperations.BulkMode.UNORDERED, table);

        // 构建查询条件
        Query query = new Query(Criteria.where("sn").is(sn)).addCriteria(
                Criteria.where("time").lt(convertDateToStr(date)));

        // 构建更新操作
        Update update = new Update();
        update.set("expireAt", date);

        // 添加更新操作到批量操作
        bulkOperations.updateMulti(query, update);

        // 执行批量操作
        return bulkOperations.execute();
    }

    private String convertDateToStr(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

}
