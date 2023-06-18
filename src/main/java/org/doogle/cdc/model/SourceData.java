package org.doogle.cdc.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SourceData {

    private String version;
    private String connector;
    private String name;
    @JsonFormat(shape = JsonFormat.Shape.NUMBER)
    @JsonProperty("ts_ms")
    private Date timestamp;
    private String snapshot;
    private String db;
    private String schema;
    private String table;
    @JsonProperty("txId")
    private Long transactionId;
    private Long lsn;
    private String xmin;
}