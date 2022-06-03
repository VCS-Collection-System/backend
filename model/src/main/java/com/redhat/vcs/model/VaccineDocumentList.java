package com.redhat.vcs.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;


import com.fasterxml.jackson.annotation.JsonManagedReference;


@Entity
@Table(
    name = "cs_vaccine_document_list"
    
)
public class VaccineDocumentList implements Serializable{

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(generator = "DOCUMENT_ID_GENERATOR", strategy = javax.persistence.GenerationType.AUTO)
    @SequenceGenerator(name = "DOCUMENT_ID_GENERATOR", sequenceName = "DOCUMENT_ID_SEQ", allocationSize = 1)
    @Column(name = "document_list_id")
    protected Long documentListId;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "document_list_id")
    private List<VaccineCardDocument> documents;

    public Long getId() {
        return documentListId;
    }

    public void setId(Long documentListId) {
        this.documentListId = documentListId;
    }
    
    @JsonManagedReference
    public List<VaccineCardDocument> getDocuments() {
        return documents;
    }

    public void setDocuments(List<VaccineCardDocument> documents) {
        this.documents = documents;
    }

    @Override
    public String toString() {
        return "VaccineDocumentList ["
                + "id=" + getId()
                + "documents=" + getDocuments()
                + "]";
    }
}
