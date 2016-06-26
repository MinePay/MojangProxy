import {Component, OnInit} from "@angular/core";
declare var highlight : any;

/**
 * Provides a main application component which bootstraps the application and all of its interfaces.
 */
@Component({
    selector:    "app",
    templateUrl: "partial/app.html"
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
