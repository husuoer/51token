package com.sharex.token.api.entity;

import java.util.Date;

public class User {

    private Integer id;
    private String mobileNum;
    private String password;
    private String salt;
    private int status;
    private String token;
    private Date lastLoginTime;
    private int loginErrCount;
    private Date frozenStartTime;
    private Date frozenEndTime;
    private Date createTime;
    private Date updateTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMobileNum() {
        return mobileNum;
    }

    public void setMobileNum(String mobileNum) {
        this.mobileNum = mobileNum;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getLoginErrCount() {
        return loginErrCount;
    }

    public void setLoginErrCount(int loginErrCount) {
        this.loginErrCount = loginErrCount;
    }

    public Date getFrozenStartTime() {
        return frozenStartTime;
    }

    public void setFrozenStartTime(Date frozenStartTime) {
        this.frozenStartTime = frozenStartTime;
    }

    public Date getFrozenEndTime() {
        return frozenEndTime;
    }

    public void setFrozenEndTime(Date frozenEndTime) {
        this.frozenEndTime = frozenEndTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}
