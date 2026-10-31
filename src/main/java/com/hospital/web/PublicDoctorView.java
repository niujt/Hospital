package com.hospital.web;

import com.hospital.entity.Doctor;
import java.util.List;
import java.util.stream.Collectors;

/** Deliberate public allowlist: never serialize account links or personal identity fields. */
public final class PublicDoctorView {
    private final Integer id;
    private final String name,department,text;
    private final Integer expert;
    private PublicDoctorView(Doctor doctor){id=doctor.getId();name=doctor.getName();department=doctor.getDepartment();text=doctor.getText();expert=doctor.getExpert();}
    public static List<PublicDoctorView> from(List<Doctor> doctors){return (doctors==null?java.util.Collections.<Doctor>emptyList():doctors).stream().map(PublicDoctorView::new).collect(Collectors.toList());}
    public Integer getId(){return id;}
    public String getName(){return name;}
    public String getDepartment(){return department;}
    public String getText(){return text;}
    public Integer getExpert(){return expert;}
}
