package com.zuby.user.zubbyrider.view.navigationdrawer.model;

import java.util.List;

public class CarServiceModel {

    /**
     * type : success
     * message : found
     * data : {"details":[{"AggregationId":1,"AggregationName":"Budget","array":[{"car_category_id":1,"category_name":"Small","cat_aggregation_id":1,"max_no_of_seats":5},{"car_category_id":2,"category_name":"Medium","cat_aggregation_id":1,"max_no_of_seats":5}]},{"AggregationId":2,"AggregationName":"Luxury","array":[{"car_category_id":3,"category_name":"Premium","cat_aggregation_id":2,"max_no_of_seats":5},{"car_category_id":4,"category_name":"Royal","cat_aggregation_id":2,"max_no_of_seats":5},{"car_category_id":5,"category_name":"SUV","cat_aggregation_id":2,"max_no_of_seats":5}]}]}
     */

    private String type;
    private String message;
    private DataBean data;

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

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        private List<DetailsBean> details;

        public List<DetailsBean> getDetails() {
            return details;
        }

        public void setDetails(List<DetailsBean> details) {
            this.details = details;
        }

        public static class DetailsBean {
            /**
             * AggregationId : 1
             * AggregationName : Budget
             * array : [{"car_category_id":1,"category_name":"Small","cat_aggregation_id":1,"max_no_of_seats":5},{"car_category_id":2,"category_name":"Medium","cat_aggregation_id":1,"max_no_of_seats":5}]
             */

            private int AggregationId;
            private String AggregationName;
            private List<ArrayBean> array;

            public int getAggregationId() {
                return AggregationId;
            }

            public void setAggregationId(int AggregationId) {
                this.AggregationId = AggregationId;
            }

            public String getAggregationName() {
                return AggregationName;
            }

            public void setAggregationName(String AggregationName) {
                this.AggregationName = AggregationName;
            }

            public List<ArrayBean> getArray() {
                return array;
            }

            public void setArray(List<ArrayBean> array) {
                this.array = array;
            }

            public static class ArrayBean {
                /**
                 * car_category_id : 1
                 * category_name : Small
                 * cat_aggregation_id : 1
                 * max_no_of_seats : 5
                 */

                private int car_category_id;
                private String category_name;
                private int cat_aggregation_id;
                private int max_no_of_seats;

                public int getCar_category_id() {
                    return car_category_id;
                }

                public void setCar_category_id(int car_category_id) {
                    this.car_category_id = car_category_id;
                }

                public String getCategory_name() {
                    return category_name;
                }

                public void setCategory_name(String category_name) {
                    this.category_name = category_name;
                }

                public int getCat_aggregation_id() {
                    return cat_aggregation_id;
                }

                public void setCat_aggregation_id(int cat_aggregation_id) {
                    this.cat_aggregation_id = cat_aggregation_id;
                }

                public int getMax_no_of_seats() {
                    return max_no_of_seats;
                }

                public void setMax_no_of_seats(int max_no_of_seats) {
                    this.max_no_of_seats = max_no_of_seats;
                }
            }
        }
    }
}
