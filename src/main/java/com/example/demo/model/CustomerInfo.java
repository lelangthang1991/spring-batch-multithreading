package com.example.demo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "Customer_Info")
public class CustomerInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String seriesReference;
    private String period;
    private String dataValue;
    private String serialStatus;
    private String units;
    private String subject;
    private String serialGroup;
    private String seriesTitle1;
    private String seriesTitle2;
    private String seriesTitle3;
    private String seriesTitle4;
    private String seriesTitle5;

//    Series_reference,Period,Data_value,STATUS,UNITS,Subject,Group,Series_title_1,Series_title_2,Series_title_3,Series_title_4,Series_title_5

    /*
{"seriesReference","period","dataValue","status","units","subject","group","seriesTitle1","seriesTitle2","seriesTitle3","seriesTitle4","seriesTitle5"}
    * */
}
