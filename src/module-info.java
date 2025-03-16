module a7.bogdan.chis {
    requires javafx.controls;
    requires org.xerial.sqlitejdbc;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;

    requires java.sql;
    requires java.xml;
    requires javafx.fxml;

    opens gui to javafx.fxml;
    opens main to javafx.fxml;

    exports main;
    exports domain;
    exports gui;
}