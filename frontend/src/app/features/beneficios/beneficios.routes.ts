import { Routes } from '@angular/router';

export const BENEFICIOS_ROUTES: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./pages/beneficios-page/beneficios-page.component').then((m) => m.BeneficiosPageComponent)
  }
];
