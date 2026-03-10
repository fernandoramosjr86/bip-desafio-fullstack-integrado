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

  it('deve retornar label e icone coerentes para status offline', () => {
    (component as any).backendStatus = 'offline';
    (component as any).checkingBackend = false;

    expect((component as any).backendStatusLabel).toBe('Backend offline');
    expect((component as any).backendStatusIcon).toBe('pi pi-times-circle');
  });
});
