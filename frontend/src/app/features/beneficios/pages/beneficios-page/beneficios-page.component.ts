import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { DropdownModule } from 'primeng/dropdown';
import { InputNumberModule } from 'primeng/inputnumber';
import { InputTextareaModule } from 'primeng/inputtextarea';
import { InputTextModule } from 'primeng/inputtext';
import { PaginatorModule, PaginatorState } from 'primeng/paginator';
import { ProgressSpinnerModule } from 'primeng/progressspinner';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToastModule } from 'primeng/toast';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { MessageService } from 'primeng/api';
import { firstValueFrom } from 'rxjs';
import {
  Beneficio,
  BeneficioRequest,
  PageResponse,
  TransferenciaRequest
} from '../../../../core/models/beneficio.model';
import { BeneficioApiService } from '../../../../core/services/beneficio-api.service';

interface TransferenciaDestinoOption {
  id: number;
  nome: string;
}

@Component({
  selector: 'app-beneficios-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    ToastModule,
    TableModule,
    TagModule,
    ButtonModule,
    PaginatorModule,
    DialogModule,
    DropdownModule,
    ProgressSpinnerModule,
    InputTextModule,
    InputTextareaModule,
    InputNumberModule,
    ToggleButtonModule
  ],
  providers: [MessageService],
  templateUrl: './beneficios-page.component.html',
  styleUrl: './beneficios-page.component.scss'
})
export class BeneficiosPageComponent implements OnInit {
  private static readonly INITIAL_PAGE_SIZE = 4;
  private static readonly INITIAL_PAGE = 0;

  protected readonly pageSizes = [4, 8, 12];
  protected beneficios: Beneficio[] = [];
  protected totalItems = 0;
  protected currentPage = BeneficiosPageComponent.INITIAL_PAGE;
  protected pageSize = BeneficiosPageComponent.INITIAL_PAGE_SIZE;
  protected loadingList = false;
  protected savingBeneficio = false;
  protected transferring = false;

  protected beneficioDialogVisible = false;
  protected transferenciaDialogVisible = false;
  protected editingId: number | null = null;
  protected transferenciaOrigem: Beneficio | null = null;
  protected transferenciaDestinos: TransferenciaDestinoOption[] = [];
  protected loadingTransferDestinos = false;

  protected readonly beneficioForm = this.fb.group({
    nome: ['', [Validators.required, Validators.maxLength(100)]],
    descricao: ['', [Validators.maxLength(255)]],
    valor: [null as number | null, [Validators.required, Validators.min(0.01)]],
    ativo: [true]
  });

  protected readonly transferenciaForm = this.fb.group({
    beneficioDestinoId: [null as number | null, [Validators.required]],
    valor: [null as number | null, [Validators.required, Validators.min(0.01)]]
  });

  constructor(
    private readonly fb: FormBuilder,
    private readonly beneficioApiService: BeneficioApiService,
    private readonly messageService: MessageService
  ) {}

  ngOnInit(): void {
    this.loadPage(BeneficiosPageComponent.INITIAL_PAGE);
  }

  protected loadPage(page: number): void {
    this.loadingList = true;
    this.beneficioApiService.listar(page, this.pageSize).subscribe({
      next: (response) => {
        const normalized = this.normalizePage(response);
        this.beneficios = normalized.items;
        this.totalItems = normalized.totalItems;
        this.currentPage = normalized.page;
        this.pageSize = normalized.size > 0 ? normalized.size : this.pageSize;
        this.loadingList = false;
      },
      error: () => {
        this.loadingList = false;
        this.notify('Falha ao carregar os benefícios.', 'error');
      }
    });
  }

  protected refresh(): void {
    this.loadPage(this.currentPage);
  }

  protected onPageChange(event: PaginatorState): void {
    const targetSize = this.asNumber(event.rows);
    const targetPage = this.asNumber(event.page);
    this.pageSize = targetSize > 0 ? targetSize : this.pageSize;
    this.loadPage(targetPage);
  }

  protected get firstRow(): number {
    return this.currentPage * this.pageSize;
  }

  protected openCreateDialog(): void {
    this.editingId = null;
    this.beneficioForm.reset({
      nome: '',
      descricao: '',
      valor: null,
      ativo: true
    });
    this.beneficioDialogVisible = true;
  }

  protected openEditDialog(beneficio: Beneficio): void {
    this.editingId = beneficio.id;
    this.beneficioForm.patchValue({
      nome: beneficio.nome,
      descricao: beneficio.descricao ?? '',
      valor: beneficio.valor,
      ativo: beneficio.ativo
    });
    this.beneficioDialogVisible = true;
  }

  protected closeBeneficioDialog(): void {
    this.beneficioDialogVisible = false;
    this.editingId = null;
    this.beneficioForm.reset({
      nome: '',
      descricao: '',
      valor: null,
      ativo: true
    });
  }

  protected saveBeneficio(): void {
    this.beneficioForm.markAllAsTouched();
    if (this.beneficioForm.invalid) {
      return;
    }

    this.savingBeneficio = true;
    const raw = this.beneficioForm.getRawValue();
    const payload: BeneficioRequest = {
      nome: (raw.nome ?? '').trim(),
      descricao: (raw.descricao ?? '').trim() ? (raw.descricao ?? '').trim() : null,
      valor: Number(raw.valor),
      ativo: raw.ativo ?? true
    };

    const request$ = this.editingId
      ? this.beneficioApiService.atualizar(this.editingId, payload)
      : this.beneficioApiService.criar(payload);

    request$.subscribe({
      next: () => {
        this.savingBeneficio = false;
        this.notify(this.editingId ? 'Benefício atualizado com sucesso.' : 'Benefício criado com sucesso.', 'success');
        this.closeBeneficioDialog();
        this.loadPage(this.currentPage);
      },
      error: (error) => {
        this.savingBeneficio = false;
        this.notify(this.extractApiMessage(error) ?? 'Falha ao salvar benefício.', 'error');
      }
    });
  }

  protected openTransferDialog(beneficio: Beneficio): void {
    this.transferenciaOrigem = beneficio;
    this.transferenciaDestinos = [];
    this.transferenciaForm.reset({
      beneficioDestinoId: null,
      valor: null
    });
    this.transferenciaDialogVisible = true;
    void this.loadTransferenciaDestinos(beneficio.id);
  }

  protected closeTransferDialog(): void {
    this.transferenciaDialogVisible = false;
    this.transferenciaOrigem = null;
    this.transferenciaDestinos = [];
    this.transferenciaForm.reset({
      beneficioDestinoId: null,
      valor: null
    });
  }

  protected transferir(): void {
    this.transferenciaForm.markAllAsTouched();
    if (this.transferenciaForm.invalid || !this.transferenciaOrigem) {
      return;
    }

    const raw = this.transferenciaForm.getRawValue();
    const origemId = this.transferenciaOrigem.id;
    if (origemId === raw.beneficioDestinoId) {
      this.notify('Origem e destino devem ser diferentes.', 'error');
      return;
    }

    this.transferring = true;
    const payload: TransferenciaRequest = {
      beneficioOrigemId: origemId,
      beneficioDestinoId: Number(raw.beneficioDestinoId),
      valor: Number(raw.valor)
    };

    this.beneficioApiService.transferir(payload).subscribe({
      next: () => {
        this.transferring = false;
        this.notify('Transferência executada com sucesso.', 'success');
        this.closeTransferDialog();
        this.loadPage(this.currentPage);
      },
      error: (error) => {
        this.transferring = false;
        this.notify(this.extractApiMessage(error) ?? 'Falha ao executar transferência.', 'error');
      }
    });
  }

  protected statusSeverity(ativo: boolean): 'success' | 'danger' {
    return ativo ? 'success' : 'danger';
  }

  protected fieldInvalid(formName: 'beneficio' | 'transferencia', controlName: string): boolean {
    const control =
      formName === 'beneficio' ? this.beneficioForm.get(controlName) : this.transferenciaForm.get(controlName);
    return !!control && control.invalid && (control.dirty || control.touched);
  }

  private notify(message: string, type: 'success' | 'error'): void {
    this.messageService.add({
      severity: type,
      summary: type === 'success' ? 'Sucesso' : 'Erro',
      detail: message,
      life: 3500
    });
  }

  private normalizePage(response: PageResponse<Beneficio>): PageResponse<Beneficio> {
    const normalizedItems = this.normalizeItems(response.items);
    return {
      items: normalizedItems,
      totalItems: this.asNumber(response.totalItems),
      page: this.asNumber(response.page),
      size: this.asNumber(response.size),
      totalPages: this.asNumber(response.totalPages),
      hasNext: !!response.hasNext,
      hasPrevious: !!response.hasPrevious
    };
  }

  private extractApiMessage(error: unknown): string | null {
    if (
      typeof error === 'object' &&
      error !== null &&
      'error' in error &&
      typeof (error as { error?: unknown }).error === 'object' &&
      (error as { error?: { message?: string } }).error?.message
    ) {
      return (error as { error: { message: string } }).error.message;
    }
    return null;
  }

  private asNumber(value: unknown): number {
    const parsed = Number(value);
    return Number.isFinite(parsed) ? parsed : 0;
  }

  private async loadTransferenciaDestinos(origemId: number): Promise<void> {
    this.loadingTransferDestinos = true;
    try {
      const beneficios = await this.fetchAllBeneficiosForTransfer();
      this.transferenciaDestinos = beneficios
        .filter((beneficio) => beneficio.id !== origemId)
        .map((beneficio) => ({
          id: beneficio.id,
          nome: beneficio.nome
        }));
    } catch {
      this.notify('Falha ao carregar benefícios para destino da transferência.', 'error');
    } finally {
      this.loadingTransferDestinos = false;
    }
  }

  private async fetchAllBeneficiosForTransfer(): Promise<Beneficio[]> {
    const allItems: Beneficio[] = [];
    const pageSize = 100;
    let page = 0;

    while (true) {
      const response = await firstValueFrom(this.beneficioApiService.listar(page, pageSize));
      const normalized = this.normalizePage(response);
      allItems.push(...normalized.items);

      if (!normalized.hasNext) {
        break;
      }

      page += 1;
    }

    return allItems;
  }

  private normalizeItems(items: unknown): Beneficio[] {
    if (!Array.isArray(items)) {
      return [];
    }

    return items
      .filter((item): item is Record<string, unknown> => typeof item === 'object' && item !== null)
      .map((item) => ({
        id: this.asNumber(item['id']),
        nome: String(item['nome'] ?? '').trim(),
        descricao: item['descricao'] == null ? null : String(item['descricao']),
        valor: this.asNumber(item['valor']),
        ativo: !!item['ativo'],
        version: this.asNumber(item['version'])
      }))
      .filter((item) => item.id > 0 && item.nome.length > 0);
  }
}
