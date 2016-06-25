import {Component, OnInit} from "@angular/core";

/**
 * Provides a main application component which bootstraps the application and all of its interfaces.
 */
@Component({
    selector:    "app",
    template: "<router-outlet></router-outlet>"
})
export class AppComponent implements OnInit {

    /**
     * {@inheritDoc}
     */
    ngOnInit() {
        console.log("Application component initialized ...");
    }
}
