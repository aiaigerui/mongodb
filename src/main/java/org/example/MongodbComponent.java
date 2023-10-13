package org.example;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.result.DeleteResult;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * @author hweig
 */
@Service
public class MongodbComponent {

    @Autowired
    private LogDao logDao;
    @Autowired
    private WorkDataDao workDataDao;

    /*@PostConstruct*/
    public void deleteLogData() throws IOException, InterruptedException {
        File file = new File("/root/gsn.xlsx");
        List<String> gsnList = readSn(new FileInputStream(file));

        for (String gsn : gsnList) {
            long startTime = System.currentTimeMillis();
            DeleteResult result = logDao.deleteLog(gsn);
            long endTime = System.currentTimeMillis();
            long castTime = endTime - startTime;

            System.out.println(
                    new Date() + " delete gsn: " + gsn + " count: " + result.getDeletedCount() + " case: " + castTime / 1000);

            Thread.sleep(200);
        }
    }

    @PostConstruct
    public void updateInverterExpireAt() throws IOException, InterruptedException {
        File file = new File("/root/sn.xlsx");
        List<String> snList = readSn(new FileInputStream(file));

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -1);
        Date expireAt = calendar.getTime();

        for (String sn : snList) {
            long startTime = System.currentTimeMillis();
            BulkWriteResult result = workDataDao.updateExpireAt(sn, expireAt);
            long endTime = System.currentTimeMillis();
            long castTime = endTime - startTime;

            System.out.println(
                    new Date() + " update sn: " + sn + " expireAt! count: " + result.getDeletedCount() + " case: " + castTime / 1000);

            Thread.sleep(100);
        }
    }

    private List<String> readSn(InputStream in) throws IOException {
        Workbook wb = WorkbookFactory.create(in);
        Sheet sheet = wb.getSheetAt(0);
        int rows = sheet.getLastRowNum();
        if (rows > 0) {
            List<String> featureList = new ArrayList<>();
            for (int i = 1; i <= rows; i++) {
                Row row = sheet.getRow(i);
                featureList.add(row.getCell(0).toString());
            }

            return featureList;
        } else {
            return new ArrayList();
        }
    }

}
