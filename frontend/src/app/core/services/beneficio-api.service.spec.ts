import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { BeneficioApiService } from './beneficio-api.service';

describe('BeneficioApiService', () => {
  let service: BeneficioApiService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [BeneficioApiService, provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(BeneficioApiService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('deve listar beneficios com page e size na query string', () => {
    const response = {
      items: [],
      totalItems: 0,
      page: 0,
      size: 4,
      totalPages: 0,
      hasNext: false,
      hasPrevious: false
    };

    service.listar(0, 4).subscribe((result) => {
      expect(result).toEqual(response);
    });

    const request = httpMock.expectOne(
      (req) =>
        req.method === 'GET' &&
        req.url === '/api/v1/beneficios' &&
        req.params.get('page') === '0' &&
        req.params.get('size') === '4'
    );
    request.flush(response);
  });

  it('deve criar beneficio via POST', () => {
    const payload = { nome: 'Beneficio Novo', descricao: null, valor: 120, ativo: true };
    const created = { id: 1, version: 0, ...payload };

    service.criar(payload).subscribe((result) => {
      expect(result).toEqual(created);
    });

    const request = httpMock.expectOne('/api/v1/beneficios');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);
    request.flush(created);
  });

  it('deve atualizar beneficio via PUT', () => {
    const payload = { nome: 'Beneficio Atualizado', descricao: 'Teste', valor: 222, ativo: false };
    const updated = { id: 2, version: 1, ...payload };

    service.atualizar(2, payload).subscribe((result) => {
      expect(result).toEqual(updated);
    });

    const request = httpMock.expectOne('/api/v1/beneficios/2');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(payload);
    request.flush(updated);
  });

  it('deve transferir valor via endpoint de transferencias', () => {
    const payload = {
      beneficioOrigemId: 1,
      beneficioDestinoId: 3,
      valor: 50
    };

    service.transferir(payload).subscribe((result) => {
      expect(result).toBeNull();
    });

    const request = httpMock.expectOne('/api/v1/beneficios/transferencias');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual(payload);
    request.flush(null);
  });
});
