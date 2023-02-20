package com.cqupt.xuetu.controller;

import com.cqupt.xuetu.response.Result;
import com.cqupt.xuetu.response.ResultFactory;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.mahout.cf.taste.common.TasteException;
import org.apache.mahout.cf.taste.impl.model.file.FileDataModel;
import org.apache.mahout.cf.taste.impl.neighborhood.NearestNUserNeighborhood;
import org.apache.mahout.cf.taste.impl.recommender.GenericUserBasedRecommender;
import org.apache.mahout.cf.taste.impl.similarity.LogLikelihoodSimilarity;
import org.apache.mahout.cf.taste.model.DataModel;
import org.apache.mahout.cf.taste.neighborhood.UserNeighborhood;
import org.apache.mahout.cf.taste.recommender.RecommendedItem;
import org.apache.mahout.cf.taste.recommender.Recommender;
import org.apache.mahout.cf.taste.similarity.UserSimilarity;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/api")
public class MahoutController {

    @RequestMapping("/recommend")
    public Result mahoutUser(@RequestBody String userid) throws JSONException {
        JSONObject json = new JSONObject(userid);
        String userId = json.getString("id");
        //数据模型
        DataModel model;
        List<RecommendedItem> list = Lists.newArrayList();
        File cvsFile;
        try {
            long startTime = System.currentTimeMillis();
//            File bookCsvFile = ResourceUtils.getFile("classpath:csv/book_cvs_file.csv");
            cvsFile = new File("/Users/mac/Desktop/xuetu-server/src/main/resources/static/file/s_class_csv_file.csv");
            model = new FileDataModel(cvsFile);
            //用户相识度算法
            UserSimilarity userSimilarity=new LogLikelihoodSimilarity(model);
            UserNeighborhood neighborhood = new NearestNUserNeighborhood(20,userSimilarity, model);
            Recommender r=new GenericUserBasedRecommender(model, neighborhood, userSimilarity);
            list = r.recommend(Long.parseLong(userId), 10);
            System.out.println(list);
            list.forEach(System.out::println);
            long endTime = System.currentTimeMillis();
            log.info((endTime-startTime) +"");
        } catch (IOException | TasteException e) {
            e.printStackTrace();
        }
        return ResultFactory.buildSuccessResult(list);
    }
}
