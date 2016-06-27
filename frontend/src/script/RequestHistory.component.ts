import { Component, Input } from '@angular/core';
import { Http, Response } from '@angular/http';

@Component({
    selector: 'request-history',
    templateUrl: 'partial/request-history.html'
})
export class RequestHistoryComponent {
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
        this.http.get('https://api.minepay.net/mojang/v1/profile/' + encodeURIComponent(this.identifier) + '/history')
            .subscribe((res : Response) => {
                const response = document.getElementById('request-history');
                response.innerText = 'HTTP/1.1 200 Ok\nContent-Type: application/json;Charset=UTF-8\n\n' + JSON.stringify(res.json(), null, 4);

                const worker = new Worker('assets/script/code.worker.js');
                worker.onmessage = function(event) {
                    console.log('Worker completed');
                    response.innerHTML = event.data;
                };
                worker.postMessage(response.innerText);
            });
    }
}
