package org.abx.model;


import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author alex
 */
@Entity
@Table(name = "campaign_data")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "CampaignData.findAll", query = "SELECT c FROM CampaignData c"),
    @NamedQuery(name = "CampaignData.findByCampaignDataId", query = "SELECT c FROM CampaignData c WHERE c.campaignDataId = :campaignDataId"),
    @NamedQuery(name = "CampaignData.findByNoWeek", query = "SELECT c FROM CampaignData c WHERE c.noWeek = :noWeek"),
    @NamedQuery(name = "CampaignData.findByWeekDescr", query = "SELECT c FROM CampaignData c WHERE c.weekDescr = :weekDescr"),
    @NamedQuery(name = "CampaignData.findByMedium", query = "SELECT c FROM CampaignData c WHERE c.medium = :medium"),
    @NamedQuery(name = "CampaignData.findByAdType", query = "SELECT c FROM CampaignData c WHERE c.adType = :adType"),
    @NamedQuery(name = "CampaignData.findByCountry", query = "SELECT c FROM CampaignData c WHERE c.country = :country"),
    @NamedQuery(name = "CampaignData.findByImpressions", query = "SELECT c FROM CampaignData c WHERE c.impressions = :impressions"),
    @NamedQuery(name = "CampaignData.findByClicks", query = "SELECT c FROM CampaignData c WHERE c.clicks = :clicks"),
    @NamedQuery(name = "CampaignData.findByCost", query = "SELECT c FROM CampaignData c WHERE c.cost = :cost")})
public class CampaignData implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "campaign_data_id", nullable = false)
    private Integer campaignDataId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "no_week", nullable = false)
    private int noWeek;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "week_descr", nullable = false, length = 255)
    private String weekDescr;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "medium", nullable = false, length = 255)
    private String medium;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "ad_type", nullable = false, length = 255)
    private String adType;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 100)
    @Column(name = "country", nullable = false, length = 100)
    private String country;
    @Basic(optional = false)
    @NotNull
    @Column(name = "impressions", nullable = false)
    private int impressions;
    @Basic(optional = false)
    @NotNull
    @Column(name = "clicks", nullable = false)
    private int clicks;
    @Basic(optional = false)
    @NotNull
    @Column(name = "cost", nullable = false)
    private double cost;
    @JoinColumn(name = "campaign_id", referencedColumnName = "campaign_id", nullable = false)
    @ManyToOne(optional = false)
    private Campaign campaignId;

    public CampaignData() {
    }

    public CampaignData(Integer campaignDataId) {
        this.campaignDataId = campaignDataId;
    }

    public CampaignData(Integer campaignDataId, int noWeek, String weekDescr, String medium, String adType, String country, int impressions, int clicks, double cost) {
        this.campaignDataId = campaignDataId;
        this.noWeek = noWeek;
        this.weekDescr = weekDescr;
        this.medium = medium;
        this.adType = adType;
        this.country = country;
        this.impressions = impressions;
        this.clicks = clicks;
        this.cost = cost;
    }

    public Integer getCampaignDataId() {
        return campaignDataId;
    }

    public void setCampaignDataId(Integer campaignDataId) {
        this.campaignDataId = campaignDataId;
    }

    public int getNoWeek() {
        return noWeek;
    }

    public void setNoWeek(int noWeek) {
        this.noWeek = noWeek;
    }

    public String getWeekDescr() {
        return weekDescr;
    }

    public void setWeekDescr(String weekDescr) {
        this.weekDescr = weekDescr;
    }

    public String getMedium() {
        return medium;
    }

    public void setMedium(String medium) {
        this.medium = medium;
    }

    public String getAdType() {
        return adType;
    }

    public void setAdType(String adType) {
        this.adType = adType;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getImpressions() {
        return impressions;
    }

    public void setImpressions(int impressions) {
        this.impressions = impressions;
    }

    public int getClicks() {
        return clicks;
    }

    public void setClicks(int clicks) {
        this.clicks = clicks;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public Campaign getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Campaign campaignId) {
        this.campaignId = campaignId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (campaignDataId != null ? campaignDataId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof CampaignData)) {
            return false;
        }
        CampaignData other = (CampaignData) object;
        if ((this.campaignDataId == null && other.campaignDataId != null) || (this.campaignDataId != null && !this.campaignDataId.equals(other.campaignDataId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.CampaignData[ campaignDataId=" + campaignDataId + " ]";
    }
    
}