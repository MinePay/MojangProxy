/// <reference path="../../node_modules/typescript/lib/lib.es6.d.ts" />
/// <reference path="../../typings/index.d.ts" />
import {bootstrap} from "@angular/platform-browser-dynamic";
import {HTTP_PROVIDERS} from "@angular/http";
import {AppComponent} from "./app.component";
import {enableProdMode} from "@angular/core";

enableProdMode();

bootstrap(AppComponent, [HTTP_PROVIDERS]);
