package com.sopt.rescat.domain.photo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sopt.rescat.domain.BaseTime;
import com.sopt.rescat.domain.Funding;
import lombok.Getter;
import lombok.NonNull;

import javax.persistence.*;

@Getter
@Entity
public class FundingPhoto extends BaseTime {
    public static final Long DEFAULT_PHOTO_ID = 1L;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @Column
    @NonNull
    private String url;

    @ManyToOne
    @JoinColumn(foreignKey = @ForeignKey(name = "fk_funding_photo_funding_idx"))
    @JsonIgnore
    private Funding funding;

    @Column
    @NonNull
    private Boolean isCertification;

    public FundingPhoto() {
        this.isCertification = false;
    }

    public FundingPhoto(@NonNull String url) {
        this.url = url;
    }

    public void setCertification() {
        this.isCertification = true;
    }

    public FundingPhoto initFunding(Funding funding) {
        this.funding = funding;
        return this;
    }
}
