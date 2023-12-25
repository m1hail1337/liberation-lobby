module ru.tinkoff.semenov {
    requires javafx.controls;
    requires javafx.fxml;
    requires io.netty.all;


    opens ru.liberation.semenov to javafx.fxml;
    opens ru.liberation.semenov.controllers to javafx.fxml;
    opens ru.liberation.semenov.enums to javafx.fxml;
    exports ru.liberation.semenov;
    exports ru.liberation.semenov.controllers;
    exports ru.liberation.semenov.enums;
}