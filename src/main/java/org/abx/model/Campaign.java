package org.abx.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.servlet.http.Part;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import org.primefaces.model.NativeUploadedFile;



@Entity
@Table(name = "campaign")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Campaign.findAll", query = "SELECT c FROM Campaign c"),
    @NamedQuery(name = "Campaign.findByCampaignId", query = "SELECT c FROM Campaign c WHERE c.campaignId = :campaignId"),
    @NamedQuery(name = "Campaign.findByCampaignDescr", query = "SELECT c FROM Campaign c WHERE c.campaignDescr = :campaignDescr"),
    @NamedQuery(name = "Campaign.findByLoadDate", query = "SELECT c FROM Campaign c WHERE c.loadDate = :loadDate"),
    @NamedQuery(name = "Campaign.findByStartDate", query = "SELECT c FROM Campaign c WHERE c.startDate = :startDate"),
    @NamedQuery(name = "Campaign.findByEndDate", query = "SELECT c FROM Campaign c WHERE c.endDate = :endDate"),
    @NamedQuery(name = "Campaign.findByFileName", query = "SELECT c FROM Campaign c WHERE c.fileName = :fileName")})
public class Campaign implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "campaign_id", nullable = false)
    private Integer campaignId;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 250)
    @Column(name = "campaign_descr", nullable = false, length = 250)
    private String campaignDescr;
    @Basic(optional = false)
    @NotNull
    @Column(name = "load_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date loadDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "start_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date startDate;
    @Basic(optional = false)
    @NotNull
    @Column(name = "end_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date endDate;
    @Basic(optional = false)
    @NotNull
    @Size(min = 1, max = 255)
    @Column(name = "file_name", nullable = false, length = 255)
    private String fileName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "campaignId")
    private Collection<CampaignData> campaignDataCollection;

    @Transient
    private NativeUploadedFile file;
    
    public Campaign() {
    }

    public Campaign(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public Campaign(Integer campaignId, String campaignDescr, Date loadDate, Date startDate, Date endDate, String fileName) {
        this.campaignId = campaignId;
        this.campaignDescr = campaignDescr;
        this.loadDate = loadDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fileName = fileName;
    }

    public Integer getCampaignId() {
        return campaignId;
    }

    public void setCampaignId(Integer campaignId) {
        this.campaignId = campaignId;
    }

    public String getCampaignDescr() {
        return campaignDescr;
    }

    public void setCampaignDescr(String campaignDescr) {
        this.campaignDescr = campaignDescr;
    }

    public Date getLoadDate() {
        return loadDate;
    }

    public void setLoadDate(Date loadDate) {
        this.loadDate = loadDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @XmlTransient
    public Collection<CampaignData> getCampaignDataCollection() {
        return campaignDataCollection;
    }

    public void setCampaignDataCollection(Collection<CampaignData> campaignDataCollection) {
        this.campaignDataCollection = campaignDataCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (campaignId != null ? campaignId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Campaign)) {
            return false;
        }
        Campaign other = (Campaign) object;
        if ((this.campaignId == null && other.campaignId != null) || (this.campaignId != null && !this.campaignId.equals(other.campaignId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Campaign[ campaignId=" + campaignId + " ]";
    }
    
    public NativeUploadedFile getFile(){
        return file;
    }

    public void setFile(NativeUploadedFile file) {
        this.file = file;
    }
}