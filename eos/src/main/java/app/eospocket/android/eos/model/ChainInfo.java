package app.eospocket.android.eos.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.gson.annotations.SerializedName;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ChainInfo {

    @JsonProperty("block_cpu_limit")
    @SerializedName("block_cpu_limit")
    public long blockCpuLimit;

    @JsonProperty("block_net_limit")
    @SerializedName("block_net_limit")
    public long blockNetLimit;

    @JsonProperty("chain_id")
    @SerializedName("chain_id")
    public String chainId;

    @JsonProperty("head_block_id")
    @SerializedName("head_block_id")
    public String headBlockId;

    @JsonProperty("head_block_num")
    @SerializedName("head_block_num")
    public long headBlockNum;

    @JsonProperty("head_block_producer")
    @SerializedName("head_block_producer")
    public String headBlockProducer;

    @JsonProperty("head_block_time")
    @SerializedName("head_block_time")
    public String headBlockTime;

    @JsonProperty("last_irreversible_block_id")
    @SerializedName("last_irreversible_block_id")
    public String lastIrreversibleBlockId;

    @JsonProperty("last_irreversible_block_num")
    @SerializedName("last_irreversible_block_num")
    public long lastIrreversibleBlockNum;

    @JsonProperty("server_version")
    @SerializedName("server_version")
    public String serverVersion;

    @JsonProperty("virtual_block_cpu_limit")
    @SerializedName("virtual_block_cpu_limit")
    public long virtualBlockCpuLimit;

    @JsonProperty("virtual_block_net_limit")
    @SerializedName("virtual_block_net_limit")
    public long virtualBlockNetLimit;
}
