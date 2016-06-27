import { Component, Input } from '@angular/core';
import { Http, Response } from '@angular/http';

@Component({
    selector: 'request-name',
    templateUrl: 'partial/request-name.html'
})
export class RequestNameComponent {
    _identifier : string = 'HighLordAkkarin';

    pollTimeout : number = null;

    constructor(private http : Http) {

    }

    @Input()
    set identifier(identifier : string) {
        this._identifier = identifier;

        if (this.pollTimeout != null) {
            window.clearTimeout(this.pollTimeout);
        }

        this.pollTimeout = window.setTimeout(() => {
            this.pollUpdate();
        }, 1000);
    }

    get identifier() : string {
        return this._identifier;
    }

    /**
     * Polls a new update from the backing API implementation.
     */
    private pollUpdate() {
        this.http.get('https://api.minepay.net/mojang/v1/name/' + encodeURIComponent(this.identifier))
            .subscribe((res : Response) => {
                const response = document.getElementById('request-name');
                response.innerText = hljs.highlightAuto('HTTP/1.1 200 Ok\nContent-Type: application/json;Charset=UTF-8\n\n' + JSON.stringify(res.json(), null, 4)).value;
            });
    }
}
