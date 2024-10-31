/*
 This file is part of MTB-PID-TO-KAFKA.

MTB-PID-TO-KAFKA - Get a CSV als Plane Text from MTB (Onkostar), extract the PIDs from the csv file, search all the oder_ids for each PID in NexusDB and produce the info as JSON in a Kafka
topic.

Copyright (C) 2024  Datenintegrationszentrum Philipps-Universität Marburg

MTB-PID-TO-KAFKA  is free software: you can redistribute it and/or modify
 it under the terms of the GNU Affero General Public License as
 published by the Free Software Foundation, either version 3 of the
 License, or (at your option) any later version.

MTB-ID-TO-KAFKA is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU Affero General Public License for more details.

 You should have received a copy of the GNU Affero General Public License
 along with this program.  If not, see <https://www.gnu.org/licenses/>
 */
package de.unimarburg.diz.mtbpidtokafka.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import lombok.Data;

@Data
public class MtbPatientInfo {


    @CsvBindByName(column = "patienten_id")
    @JsonProperty("patienten_id")
    //@CsvBindByName(column = "Patienten-ID")
    private String patientenId;

    @CsvBindByName(column = "pid_gesperrt")
    @JsonProperty("pid_gesperrt")
    private int pidGesperrt;

    @CsvBindByName(column = "tumor_id")
    @JsonProperty("tumor_id")
    //@CsvBindByName(column = "Tumoridentifikator")
    private String tumorId;

    @CsvBindByName(column = "erkrankung_sid")
    @JsonProperty("erkrankung_sid")
    private String sid;

    @CsvBindByName (column = "erkrankung_guid" )
    @JsonProperty("erkrankung_guid")
    private String guid;

    @CsvBindByName (column = "erkrankung_revision")
    @JsonProperty("erkrankung_revision")
    private String revision;

    @CsvBindByName (column = "einsendenummer")
    @JsonProperty("einsendennummer")
    private String einsendennummer;

    @CsvBindByName (column = "diagnosedatum")
    @JsonProperty("diagnose_datum")
    private String diagnoseDatum;

    @CsvBindByName (column = "mig_referenz_tumor_id")
    @JsonProperty("migReferenzTumorId")
    private String migReferenzTumorId ;

}


