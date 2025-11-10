import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HomePageComponent } from './component/pages/home-page/home-page.component';
import { FooterComponent } from './component/pages/footer/footer.component';
import { PageModule } from './component/pages/page/page.module';
import { HttpClientModule } from '@angular/common/http';
import { AdminDashboardComponent } from './component/admin-package/admin-dashboard/admin-dashboard.component';
import { TemplateComponent } from './component/admin-package/cockpit/client/template/template.component';
import { CreateQuoteComponent } from './component/quote-package/web-service/create-quote/create-quote.component';
import { CustomTemplateComponent } from './component/customer-package/custom-template/custom-template.component';
import { DeleteClientComponent } from './component/customer-package/web-service/delete-client/delete-client.component';
import { UpdateDevisComponent } from './component/devis-package/web-service/update-devis/update-devis.component';

@NgModule({
  declarations: [
    AppComponent,
    HomePageComponent,
    FooterComponent,
    AdminDashboardComponent,
    TemplateComponent,
    CreateQuoteComponent,
    CustomTemplateComponent,
    DeleteClientComponent,
    UpdateDevisComponent,
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
