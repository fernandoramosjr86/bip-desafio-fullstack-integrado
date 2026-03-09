import { Routes } from '@angular/router';
import { BeneficiosPageComponent } from './features/beneficios/pages/beneficios-page/beneficios-page.component';

export const routes: Routes = [
  {
    path: '',
    component: BeneficiosPageComponent
  },
  {
    path: '**',
    redirectTo: ''
  }
];
