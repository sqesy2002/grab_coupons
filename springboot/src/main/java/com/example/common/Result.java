package com.example.common;

public class Result {
    private String code;//返回码
    private String msg;//返回信息
    private Object data;//返回数据


    public static Result success(){
        Result result=new Result();
        result.setCode("200");
        result.setMsg("请求成功");
        return result;
    }

    //请求成功
    public static Result success(Object data){
        Result result = success();
        result.setData(data);
        return result;
    }

    //请求失败
    public static Result error(){
        Result result=new Result();
        result.setCode("500");
        result.setMsg("系统错误");
        return result;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}