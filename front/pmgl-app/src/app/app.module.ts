import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomePageComponent } from './component/pages/home-page/home-page.component';
import { FooterComponent } from './component/pages/footer/footer.component';
import { PageModule } from './component/pages/page/page.module';
import { HttpClientModule } from '@angular/common/http';
import { LoginComponent } from './component/customer-package/authenticate/login/login.component';
import { AllCustomerComponent } from './component/customer-package/web-service/all-customer/all-customer.component';

@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    FooterComponent,
    LoginComponent,
    AllCustomerComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    PageModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
