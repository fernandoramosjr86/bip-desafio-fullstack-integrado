import { HttpClient } from '@angular/common/http';
import { ConfirmationService, MessageService } from 'primeng/api';
import { of, throwError } from 'rxjs';
import { AppComponent } from './app.component';

describe('AppComponent', () => {
  let component: AppComponent;
  let httpSpy: jasmine.SpyObj<HttpClient>;
  let messageSpy: jasmine.SpyObj<MessageService>;
  let confirmationSpy: jasmine.SpyObj<ConfirmationService>;

  beforeEach(() => {
    httpSpy = jasmine.createSpyObj<HttpClient>('HttpClient', ['get']);
    messageSpy = jasmine.createSpyObj<MessageService>('MessageService', ['add']);
    confirmationSpy = jasmine.createSpyObj<ConfirmationService>('ConfirmationService', ['confirm']);

    component = new AppComponent(httpSpy, messageSpy, confirmationSpy);
  });

  it('deve atualizar status para online quando health retorna UP', () => {
    httpSpy.get.and.returnValue(of({ status: 'UP' }));

    (component as any).refreshBackendStatus();

    expect((component as any).backendStatus).toBe('online');
    expect((component as any).checkingBackend).toBeFalse();
  });

  it('deve atualizar status para offline quando health falha', () => {
    httpSpy.get.and.returnValue(throwError(() => new Error('offline')));

    (component as any).refreshBackendStatus();

    expect((component as any).backendStatus).toBe('offline');
    expect((component as any).checkingBackend).toBeFalse();
  });

  it('deve manter chamada bloqueada quando ja estiver verificando backend', () => {
    (component as any).checkingBackend = true;

    (component as any).refreshBackendStatus();

    expect(httpSpy.get).not.toHaveBeenCalled();
  });

  it('deve marcar offline quando endpoint responde status diferente de UP', () => {
    httpSpy.get.and.returnValue(of({ status: 'DOWN' }));

    (component as any).refreshBackendStatus();

    expect((component as any).backendStatus).toBe('offline');
    expect((component as any).checkingBackend).toBeFalse();
  });

  it('deve emitir notificacao quando backend cai', () => {
    (component as any).previousStableBackendStatus = 'online';

    (component as any).applyBackendStatus('offline');

    expect(messageSpy.add).toHaveBeenCalled();
  });

  it('deve pedir confirmacao quando backend volta', () => {
    (component as any).previousStableBackendStatus = 'offline';

    (component as any).applyBackendStatus('online');

    expect(confirmationSpy.confirm).toHaveBeenCalled();
    const args = confirmationSpy.confirm.calls.mostRecent().args[0];
    expect(args.message).toContain('voltou');
  });

  it('deve permitir seguir sem atualizar quando usuario rejeita confirmacao', () => {
    (component as any).previousStableBackendStatus = 'offline';

    (component as any).applyBackendStatus('online');

    const args = confirmationSpy.confirm.calls.mostRecent().args[0];
    expect(typeof args.reject).toBe('function');
    args.reject?.();

    const addArg = messageSpy.add.calls.mostRecent().args[0];
    expect(addArg.severity).toBe('info');
    expect(addArg.summary).toBe('Sem atualização');
  });

  it('deve retornar label de verificacao quando status ainda esta em check inicial', () => {
    (component as any).backendStatus = 'checking';
    (component as any).checkingBackend = true;

    expect((component as any).backendStatusLabel).toBe('Verificando backend...');
  });

  it('deve retornar icone de spinner durante verificacao inicial', () => {
    (component as any).backendStatus = 'checking';
    (component as any).checkingBackend = true;

    expect((component as any).backendStatusIcon).toBe('pi pi-spin pi-spinner');
  });

  it('nao deve disparar notificacoes quando nao houver transicao relevante', () => {
    (component as any).previousStableBackendStatus = null;

    (component as any).applyBackendStatus('online');

    expect(messageSpy.add).not.toHaveBeenCalled();
    expect(confirmationSpy.confirm).not.toHaveBeenCalled();
  });

  it('deve cancelar polling no ngOnDestroy quando houver subscription ativa', () => {
    const unsubscribe = jasmine.createSpy('unsubscribe');
    (component as any).pollSubscription = { unsubscribe } as any;

    component.ngOnDestroy();

    expect(unsubscribe).toHaveBeenCalled();
  });

  it('deve retornar label e icone coerentes para status offline', () => {
    (component as any).backendStatus = 'offline';
    (component as any).checkingBackend = false;

    expect((component as any).backendStatusLabel).toBe('Backend offline');
    expect((component as any).backendStatusIcon).toBe('pi pi-times-circle');
  });
});
