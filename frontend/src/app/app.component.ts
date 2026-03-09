import { HttpClient } from '@angular/common/http';
import { Component, OnDestroy, OnInit } from '@angular/core';
import { RouterLink, RouterLinkActive, RouterOutlet } from '@angular/router';
import { ConfirmationService, MessageService } from 'primeng/api';
import { ButtonModule } from 'primeng/button';
import { ConfirmDialogModule } from 'primeng/confirmdialog';
import { ToastModule } from 'primeng/toast';
import { Subscription, interval } from 'rxjs';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink, RouterLinkActive, ButtonModule, ToastModule, ConfirmDialogModule],
  providers: [MessageService, ConfirmationService],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss'
})
export class AppComponent implements OnInit, OnDestroy {
  private static readonly STATUS_POLL_MS = 15000;
  private static readonly BACKEND_HEALTH_ENDPOINT = '/api/v1/beneficios?page=0&size=1';

  private pollSubscription?: Subscription;
  private previousStableBackendStatus: 'online' | 'offline' | null = null;
  protected backendStatus: 'checking' | 'online' | 'offline' = 'checking';
  protected checkingBackend = false;

  constructor(
    private readonly http: HttpClient,
    private readonly messageService: MessageService,
    private readonly confirmationService: ConfirmationService
  ) {}

  ngOnInit(): void {
    this.refreshBackendStatus();
    this.pollSubscription = interval(AppComponent.STATUS_POLL_MS).subscribe(() => this.refreshBackendStatus());
  }

  ngOnDestroy(): void {
    this.pollSubscription?.unsubscribe();
  }

  protected refreshBackendStatus(): void {
    if (this.checkingBackend) {
      return;
    }

    this.checkingBackend = true;
    this.http.get<{ items?: unknown[] }>(AppComponent.BACKEND_HEALTH_ENDPOINT).subscribe({
      next: (response) => {
        const nextStatus: 'online' | 'offline' = Array.isArray(response?.items) ? 'online' : 'offline';
        this.applyBackendStatus(nextStatus);
        this.checkingBackend = false;
      },
      error: () => {
        this.applyBackendStatus('offline');
        this.checkingBackend = false;
      }
    });
  }

  protected get backendStatusLabel(): string {
    if (this.checkingBackend && this.backendStatus === 'checking') {
      return 'Verificando backend...';
    }

    return this.backendStatus === 'online' ? 'Backend online' : 'Backend offline';
  }

  protected get backendStatusIcon(): string {
    if (this.checkingBackend && this.backendStatus === 'checking') {
      return 'pi pi-spin pi-spinner';
    }

    return this.backendStatus === 'online' ? 'pi pi-check-circle' : 'pi pi-times-circle';
  }

  private applyBackendStatus(nextStatus: 'online' | 'offline'): void {
    const previous = this.previousStableBackendStatus;
    this.backendStatus = nextStatus;
    this.previousStableBackendStatus = nextStatus;

    if (previous === 'online' && nextStatus === 'offline') {
      this.messageService.add({
        severity: 'error',
        summary: 'Backend indisponível',
        detail: 'A conexão com o backend foi perdida.',
        life: 5000
      });
    }

    if (previous === 'offline' && nextStatus === 'online') {
      this.confirmationService.confirm({
        header: 'Backend restabelecido',
        message: 'A conexão com o backend voltou. Deseja atualizar a tela agora?',
        icon: 'pi pi-question-circle',
        acceptLabel: 'Atualizar',
        rejectLabel: 'Continuar sem atualizar',
        accept: () => {
          window.location.reload();
        },
        reject: () => {
          this.messageService.add({
            severity: 'info',
            summary: 'Sem atualização',
            detail: 'Você optou por continuar sem recarregar a tela.',
            life: 3500
          });
        }
      });
    }
  }
}
