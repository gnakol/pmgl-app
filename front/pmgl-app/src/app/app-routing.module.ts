// app-routing.module.ts
import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './component/customer-package/authenticate/login/login.component';
import { CustomerDashboardComponent } from './component/admin-package/customer-dashboard/customer-dashboard.component';
import { AdminDashboardComponent } from './component/admin-package/admin-dashboard/admin-dashboard.component';
import { HomePageComponent } from './component/pages/home-page/home-page.component';
import { AdminLoginPage } from './component/admin-package/security/admin-login-page/admin-login-page.component';
import { AdminGuard } from './component/admin-package/security/guards/admin.guard';
import { QuoteTemplateComponent } from './component/quote-package/quote-template/quote-template.component';
import { CustomTemplateComponent } from './component/customer-package/custom-template/custom-template.component';

const routes: Routes = [
  { path: '', component: HomePageComponent },
  { path: 'home-page', component: HomePageComponent },

  // Customer
  { path: 'login', component: LoginComponent },
  { path: 'template-customer', component : CustomTemplateComponent},

  // Admin
  { path: 'admin-login', component: AdminLoginPage },
  { path: 'dashboard-admin', component: AdminDashboardComponent, canActivate: [AdminGuard] },
  { path: 'quote-template', component : QuoteTemplateComponent, canActivate : [AdminGuard]},
  { path: 'dashboard-customer-admin', component: CustomerDashboardComponent},

  // *** AJOUT ICI ***
  {
    path: 'client-invite',
    loadComponent: () =>
      import('./component/customer-package/web-service/confirm-create-account/confirm-create-account.component')
        .then(m => m.ConfirmCreateAccountComponent)
  },

  { path: '**', redirectTo: '' }
];


@NgModule({
  imports: [RouterModule.forRoot(routes, { scrollPositionRestoration: 'enabled' })],
  exports: [RouterModule]
})
export class AppRoutingModule {}
