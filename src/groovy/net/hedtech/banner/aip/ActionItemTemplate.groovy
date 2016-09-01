package net.hedtech.banner.aip

import javax.persistence.*

@NamedQueries(value = [
        @NamedQuery(name = "ActionItemTemplate.fetchActionItemTemplates",
            query = """
            FROM ActionItemTemplate a
            """),
        @NamedQuery(name = "ActionItemTemplate.fetchActionItemTemplateById",
        query = """
        FROM ActionItemTemplate a
        WHERE a.id = :myId
        """)
])

@Entity
@Table(name = "GCBPBTR")

class ActionItemTemplate implements Serializable{

    /**
     * Surrogate ID for GVBPBTR
     */

    @Id
    @Column(name = "GCBPBTR_SURROGATE_ID")
    @SequenceGenerator(name = "GCBPBTR_SEQ_GEN", allocationSize = 1, sequenceName = "GCBPBTR_SURROGATE_ID_SEQUENCE")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "GCBPBTR_SEQ_GEN")
    Long id


    /**
     * Name of the action item template
     */
    @Column(name = "GCBPBTR_TEMPLATE_NAME")
    String title

    /**
     *
     */
    @Column(name = "GCBPBTR_PAGE_ID")
    Long pageId

    /**
     *
     */
    @Column(name = "GCBPBTR_SOURCE_IND")
    String sourceInd

    /**
     *
     */
    @Column(name = "GCBPBTR_USER_ID")
    String userId

    /**
     *
     */
    @Column(name = "GCBPBTR_SYSTEM_REQUIRED")
    String systemRequired

    /**
     *
     */
    @Column(name = "GCBPBTR_ACTIVE_IND")
    String activeInd

    /**
     *
     */
    @Column(name = "GCBPBTR_ACTIVITY_DATE")
    Date activityDate

    /**
     *
     */
    @Version
    @Column(name = "GCBPBTR_VERSION")
    Long version

    /**
     *
     */
    @Column(name = "GCBPBTR_DATA_ORIGIN")
    String dataOrigin

    /**
     *
     */
    @Column(name = "GCBPBTR_VPDI_CODE")
    String vpdiCode

    @Override
    public String toString() {
        return "ActionItemTemplate{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", pageId='" + pageId + '\'' +
                ", sourceInd='" + sourceInd + '\'' +
                ", userId='" + userId + '\'' +
                ", systemRequired='" + systemRequired + '\'' +
                ", activeInd'" + activeInd + '\'' +
                ", activityDate='" + activityDate + '\'' +
                ", version='" + version + '\'' +
                ", dataOrigin='" + dataOrigin + '\'' +
                ", vpdiCode='" + vpdiCode + '\'' +
                '}'
    }

    boolean equals(o) {
        if(this.is(o)) return true
        if(!(o instanceof ActionItemTemplate)) return false

        ActionItemTemplate that = (ActionItemTemplate) o

        if (id != that.id) return false
        if (title != that.title) return false
        if (sourceInd != that.sourceInd) return false
        if (userId != that.userId) return false
        if (systemRequired != that.systemRequired) return false
        if (activeInd != that.activeInd) return false
        if (activityDate != that.activityDate) return false
        if (version != that.version) return false
        if (dataOrigin != that.dataOrigin) return false
        if (vpdiCode != that.vpdiCode) return false

        return true
    }

    int hashCode() {
        int result = 0
        result = 31 * result + (id != null ? id.hashCode() : 0)
        result = 31 * result + (title != null ? title.hashCode() : 0)
        result = 31 * result + (sourceInd != null ? sourceInd.hashCode() : 0)
        result = 31 * result + (userId != null ? userId.hashCode() : 0)
        result = 31 * result + (systemRequired != null ? systemRequired.hashCode() : 0)
        result = 31 * result + (activeInd != null ? activeInd.hashCode() : 0)
        result = 31 * result + (activityDate != null ? activityDate.hashCode() : 0)
        result = 31 * result + (version != null ? version.hashCode() : 0)
        result = 31 * result + (dataOrigin != null ? dataOrigin.hashCode() : 0)
        result = 31 * result + (vpdiCode != null ? vpdiCode.hashCode() : 0)
        return result
    }

    static constraints = {
        title(blank: false, nullable: false)
        sourceInd(blank: false, nullable: false)
        userId(blank: false, nullable: false)
        systemRequired(blank: false, nullable: false)
        activeInd(nullable: false)
        activityDate(blank: false, nullable: false)
        dataOrigin(nullable: false)
    }

    public static def fetchActionItemTemplates() {
        ActionItemTemplate.withSession {session ->
            List actionItemTemplates = session.getNamedQuery('ActionItemTemplate.fetchActionItemTemplates').list()
            return actionItemTemplates
        }
    }
    public static def fetchActionItemTemplateById( Long myId ) {
        ActionItemTemplate.withSession {session ->
            List actionItemTemplates = session.getNamedQuery('ActionItemTemplate.fetchActionItemTemplateById').setLong('myId', myId)?.list()
            return actionItemTemplates
        }
    }
}
