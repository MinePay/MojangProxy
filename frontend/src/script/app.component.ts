import {Component, OnInit} from "@angular/core";
import {RequestProfileComponent} from "./RequestProfile.component";
import {RequestHistoryComponent} from "./RequestHistory.component";
declare var highlight : any;

/**
 * Provides a main application component which bootstraps the application and all of its interfaces.
 */
@Component({
    selector:    "app",
    templateUrl: "partial/app.html",
    directives: [
        RequestHistoryComponent,
        RequestProfileComponent
    ]
})
export class AppComponent implements OnInit {

    /**
     * {@inheritDoc}
     */
    ngOnInit() {
        console.log("Application component initialized ...");
        highlight();
    }
}
