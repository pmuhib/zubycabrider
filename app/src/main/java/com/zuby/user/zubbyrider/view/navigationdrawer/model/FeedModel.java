package com.zuby.user.zubbyrider.view.navigationdrawer.model;

import java.util.List;

public class FeedModel {

    /**
     * type : success
     * message : Record found
     * data : [{"feed_id":1,"feed_title":"News of today","feed_html_links":"https://www.google.co.in/","feed_content":"It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters","feed_type":"news","post_date":"2018-05-08 00:00:00","end_date":"2018-05-31 00:00:00","city_code":"noida","postal_code":"201301","priority_text":"Test","priority_score":10,"feed_Priority_id":1}]
     */

    private String type;
    private String message;
    private List<DataBean> data;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * feed_id : 1
         * feed_title : News of today
         * feed_html_links : https://www.google.co.in/
         * feed_content : It is a long established fact that a reader will be distracted by the readable content of a page when looking at its layout. The point of using Lorem Ipsum is that it has a more-or-less normal distribution of letters
         * feed_type : news
         * post_date : 2018-05-08 00:00:00
         * end_date : 2018-05-31 00:00:00
         * city_code : noida
         * postal_code : 201301
         * priority_text : Test
         * priority_score : 10
         * feed_Priority_id : 1
         */

        private int feed_id;
        private String feed_title;
        private String feed_html_links;
        private String feed_content;
        private String feed_type;
        private String post_date;
        private String end_date;
        private String city_code;
        private String postal_code;
        private String priority_text;
        private int priority_score;
        private int feed_Priority_id;

        public int getFeed_id() {
            return feed_id;
        }

        public void setFeed_id(int feed_id) {
            this.feed_id = feed_id;
        }

        public String getFeed_title() {
            return feed_title;
        }

        public void setFeed_title(String feed_title) {
            this.feed_title = feed_title;
        }

        public String getFeed_html_links() {
            return feed_html_links;
        }

        public void setFeed_html_links(String feed_html_links) {
            this.feed_html_links = feed_html_links;
        }

        public String getFeed_content() {
            return feed_content;
        }

        public void setFeed_content(String feed_content) {
            this.feed_content = feed_content;
        }

        public String getFeed_type() {
            return feed_type;
        }

        public void setFeed_type(String feed_type) {
            this.feed_type = feed_type;
        }

        public String getPost_date() {
            return post_date;
        }

        public void setPost_date(String post_date) {
            this.post_date = post_date;
        }

        public String getEnd_date() {
            return end_date;
        }

        public void setEnd_date(String end_date) {
            this.end_date = end_date;
        }

        public String getCity_code() {
            return city_code;
        }

        public void setCity_code(String city_code) {
            this.city_code = city_code;
        }

        public String getPostal_code() {
            return postal_code;
        }

        public void setPostal_code(String postal_code) {
            this.postal_code = postal_code;
        }

        public String getPriority_text() {
            return priority_text;
        }

        public void setPriority_text(String priority_text) {
            this.priority_text = priority_text;
        }

        public int getPriority_score() {
            return priority_score;
        }

        public void setPriority_score(int priority_score) {
            this.priority_score = priority_score;
        }

        public int getFeed_Priority_id() {
            return feed_Priority_id;
        }

        public void setFeed_Priority_id(int feed_Priority_id) {
            this.feed_Priority_id = feed_Priority_id;
        }
    }
}
