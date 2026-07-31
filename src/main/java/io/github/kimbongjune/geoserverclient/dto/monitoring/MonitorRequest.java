package io.github.kimbongjune.geoserverclient.dto.monitoring;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Objects;

/**
 * DTO for a monitoring request (detail).
 *
 * <p>Maps the {@code "org.geoserver.monitor.RequestData"} object returned by
 * {@code GET /rest/monitor/requests/{id}}.
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

    /** Constructs an empty {@code MonitorRequest} for deserialization. */
    public MonitorRequest() {}

    /** @return the request ID */
    public long getId() {
        return id;
    }
    /** @return the internal request ID */
    public long getInternalid() {
        return internalid;
    }
    /** @return the request status (e.g. {@code "FINISHED"}, {@code "FAILED"}) */
    public String getStatus() {
        return status;
    }
    /** @return the request category (e.g. {@code "OWS"}) */
    public String getCategory() {
        return category;
    }
    /** @return the request path */
    public String getPath() {
        return path;
    }
    /** @return the request body */
    public String getBody() {
        return body;
    }
    /** @return the body content length in bytes */
    public long getBodyContentLength() {
        return bodyContentLength;
    }
    /** @return the HTTP method (e.g. {@code "GET"}, {@code "POST"}) */
    public String getHttpMethod() {
        return httpMethod;
    }
    /** @return the request start time as an ISO-8601 string */
    public String getStartTime() {
        return startTime;
    }
    /** @return the request end time as an ISO-8601 string */
    public String getEndTime() {
        return endTime;
    }
    /** @return the total processing time in milliseconds */
    public long getTotalTime() {
        return totalTime;
    }
    /** @return the remote client IP address */
    public String getRemoteAddr() {
        return remoteAddr;
    }
    /** @return the remote client hostname */
    public String getRemoteHost() {
        return remoteHost;
    }
    /** @return the remote authenticated user */
    public String getRemoteUser() {
        return remoteUser;
    }
    /** @return the remote client latitude */
    public double getRemoteLat() {
        return remoteLat;
    }
    /** @return the remote client longitude */
    public double getRemoteLon() {
        return remoteLon;
    }
    /** @return the GeoServer host name */
    public String getHost() {
        return host;
    }
    /** @return the GeoServer internal host name */
    public String getInternalHost() {
        return internalHost;
    }
    /** @return the response content length in bytes */
    public long getResponseLength() {
        return responseLength;
    }
    /** @return the response content type */
    public String getResponseContentType() {
        return responseContentType;
    }
    /** @return the HTTP response status code */
    public int getResponseStatus() {
        return responseStatus;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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
