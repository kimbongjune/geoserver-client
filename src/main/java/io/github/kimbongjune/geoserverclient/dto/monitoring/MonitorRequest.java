package io.github.kimbongjune.geoserverclient.dto.monitoring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for a monitoring request (detail).
 *
 * <p>Maps the {@code "org.geoserver.monitor.RequestData"} object returned by
 * {@code GET /rest/monitor/requests/{id}}.</p>
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@JsonIgnoreProperties(ignoreUnknown = true)
public class MonitorRequest {

    private long id;
    private long internalid;
    private String status;
    private String category;
    private String path;
    private String body;
    private long bodyContentLength;
    private String httpMethod;
    private String startTime;
    private String endTime;
    private long totalTime;
    private String remoteAddr;
    private String remoteHost;
    private String remoteUser;
    private double remoteLat;
    private double remoteLon;
    private String host;
    private String internalHost;
    private long responseLength;
    private String responseContentType;
    private int responseStatus;

    public MonitorRequest() {}

    public long getId()                   { return id; }
    public long getInternalid()            { return internalid; }
    public String getStatus()             { return status; }
    public String getCategory()           { return category; }
    public String getPath()               { return path; }
    public String getBody()               { return body; }
    public long getBodyContentLength()    { return bodyContentLength; }
    public String getHttpMethod()         { return httpMethod; }
    public String getStartTime()          { return startTime; }
    public String getEndTime()            { return endTime; }
    public long getTotalTime()            { return totalTime; }
    public String getRemoteAddr()         { return remoteAddr; }
    public String getRemoteHost()         { return remoteHost; }
    public String getRemoteUser()         { return remoteUser; }
    public double getRemoteLat()          { return remoteLat; }
    public double getRemoteLon()          { return remoteLon; }
    public String getHost()               { return host; }
    public String getInternalHost()       { return internalHost; }
    public long getResponseLength()       { return responseLength; }
    public String getResponseContentType(){ return responseContentType; }
    public int getResponseStatus()        { return responseStatus; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MonitorRequest that = (MonitorRequest) o;
        return Objects.equals(id, that.id)
                && Objects.equals(internalid, that.internalid)
                && Objects.equals(status, that.status)
                && Objects.equals(category, that.category)
                && Objects.equals(path, that.path)
                && Objects.equals(body, that.body)
                && Objects.equals(bodyContentLength, that.bodyContentLength)
                && Objects.equals(httpMethod, that.httpMethod)
                && Objects.equals(startTime, that.startTime)
                && Objects.equals(endTime, that.endTime)
                && Objects.equals(totalTime, that.totalTime)
                && Objects.equals(remoteAddr, that.remoteAddr)
                && Objects.equals(remoteHost, that.remoteHost)
                && Objects.equals(remoteUser, that.remoteUser)
                && Objects.equals(remoteLat, that.remoteLat)
                && Objects.equals(remoteLon, that.remoteLon)
                && Objects.equals(host, that.host)
                && Objects.equals(internalHost, that.internalHost)
                && Objects.equals(responseLength, that.responseLength)
                && Objects.equals(responseContentType, that.responseContentType)
                && Objects.equals(responseStatus, that.responseStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, internalid, status, category, path, body, bodyContentLength, httpMethod, startTime, endTime, totalTime, remoteAddr, remoteHost, remoteUser, remoteLat, remoteLon, host, internalHost, responseLength, responseContentType, responseStatus);
    }

    @Override
    public String toString() {
        return "MonitorRequest{" +
                "id=" + id +
                ", internalid=" + internalid +
                ", status=" + status +
                ", category=" + category +
                ", path=" + path +
                ", body=" + body +
                ", bodyContentLength=" + bodyContentLength +
                ", httpMethod=" + httpMethod +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", totalTime=" + totalTime +
                ", remoteAddr=" + remoteAddr +
                ", remoteHost=" + remoteHost +
                ", remoteUser=" + remoteUser +
                ", remoteLat=" + remoteLat +
                ", remoteLon=" + remoteLon +
                ", host=" + host +
                ", internalHost=" + internalHost +
                ", responseLength=" + responseLength +
                ", responseContentType=" + responseContentType +
                ", responseStatus=" + responseStatus +
                '}';
    }
}
