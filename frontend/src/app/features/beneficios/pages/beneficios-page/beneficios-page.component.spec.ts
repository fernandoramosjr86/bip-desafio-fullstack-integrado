import { FormBuilder } from '@angular/forms';
import { of, throwError } from 'rxjs';
import { MessageService } from 'primeng/api';
import { Beneficio, PageResponse } from '../../../../core/models/beneficio.model';
import { BeneficioApiService } from '../../../../core/services/beneficio-api.service';
import { BeneficiosPageComponent } from './beneficios-page.component';

function pageResponse(
  items: Beneficio[],
  page = 0,
  size = 4,
  totalItems = items.length,
  hasNext = false
): PageResponse<Beneficio> {
  return {
    items,
    totalItems,
    page,
    size,
    totalPages: hasNext ? page + 2 : page + 1,
    hasNext,
    hasPrevious: page > 0
  };
}

describe('BeneficiosPageComponent', () => {
  let component: BeneficiosPageComponent;
  let apiSpy: jasmine.SpyObj<BeneficioApiService>;
  let messageSpy: jasmine.SpyObj<MessageService>;

  beforeEach(() => {
    apiSpy = jasmine.createSpyObj<BeneficioApiService>('BeneficioApiService', ['listar', 'criar', 'atualizar', 'transferir']);
    messageSpy = jasmine.createSpyObj<MessageService>('MessageService', ['add']);

    component = new BeneficiosPageComponent(new FormBuilder(), apiSpy, messageSpy);
  });

  it('deve carregar a primeira pagina ao inicializar', () => {
    const beneficio: Beneficio = {
      id: 1,
      nome: 'Beneficio A',
      descricao: 'Descricao A',
      valor: 100,
      ativo: true,
      version: 0
    };
    apiSpy.listar.and.returnValue(of(pageResponse([beneficio])));

    component.ngOnInit();

    expect(apiSpy.listar).toHaveBeenCalledWith(0, 4);
    expect((component as any).beneficios).toEqual([beneficio]);
    expect((component as any).totalItems).toBe(1);
  });

  it('deve normalizar e filtrar itens invalidos na listagem', () => {
    const dirtyResponse = {
      items: [
        { id: 1, nome: ' Beneficio X ', descricao: null, valor: '80', ativo: true, version: 2 },
        { id: 0, nome: 'Invalido', descricao: null, valor: 12, ativo: true, version: 0 },
        { id: 3, nome: '   ', descricao: null, valor: 9, ativo: true, version: 0 }
      ],
      totalItems: '3',
      page: '0',
      size: '4',
      totalPages: '1',
      hasNext: false,
      hasPrevious: false
    } as unknown as PageResponse<Beneficio>;
    apiSpy.listar.and.returnValue(of(dirtyResponse));

    (component as any).loadPage(0);

    expect((component as any).beneficios).toEqual([
      {
        id: 1,
        nome: 'Beneficio X',
        descricao: null,
        valor: 80,
        ativo: true,
        version: 2
      }
    ]);
    expect((component as any).totalItems).toBe(3);
    expect((component as any).pageSize).toBe(4);
  });

  it('deve notificar erro ao falhar carregamento da lista', () => {
    apiSpy.listar.and.returnValue(throwError(() => new Error('erro')));

    (component as any).loadPage(0);

    expect(messageSpy.add).toHaveBeenCalled();
    expect((component as any).loadingList).toBeFalse();
  });

  it('deve criar beneficio com payload normalizado', () => {
    apiSpy.criar.and.returnValue(
      of({
        id: 9,
        nome: 'Novo',
        descricao: null,
        valor: 25,
        ativo: true,
        version: 0
      })
    );
    apiSpy.listar.and.returnValue(of(pageResponse([])));
    spyOn<any>(component, 'loadPage').and.stub();

    (component as any).openCreateDialog();
    (component as any).beneficioForm.setValue({
      nome: '  Novo  ',
      descricao: '   ',
      valor: 25,
      ativo: true
    });

    (component as any).saveBeneficio();

    expect(apiSpy.criar).toHaveBeenCalledWith({
      nome: 'Novo',
      descricao: null,
      valor: 25,
      ativo: true
    });
    expect((component as any).beneficioDialogVisible).toBeFalse();
    expect((component as any).loadPage).toHaveBeenCalledWith(0);
  });

  it('nao deve transferir quando origem e destino forem iguais', () => {
    (component as any).transferenciaOrigem = {
      id: 10,
      nome: 'Origem',
      descricao: null,
      valor: 500,
      ativo: true,
      version: 0
    } as Beneficio;
    (component as any).transferenciaForm.setValue({
      beneficioDestinoId: 10,
      valor: 1
    });

    (component as any).transferir();

    expect(apiSpy.transferir).not.toHaveBeenCalled();
    expect(messageSpy.add).toHaveBeenCalled();
  });

  it('deve executar transferencia com sucesso', () => {
    apiSpy.transferir.and.returnValue(of(void 0));
    spyOn<any>(component, 'loadPage').and.stub();

    (component as any).transferenciaOrigem = {
      id: 1,
      nome: 'Origem',
      descricao: null,
      valor: 100,
      ativo: true,
      version: 0
    } as Beneficio;
    (component as any).transferenciaDialogVisible = true;
    (component as any).transferenciaForm.setValue({
      beneficioDestinoId: 2,
      valor: 30
    });

    (component as any).transferir();

    expect(apiSpy.transferir).toHaveBeenCalledWith({
      beneficioOrigemId: 1,
      beneficioDestinoId: 2,
      valor: 30
    });
    expect((component as any).transferenciaDialogVisible).toBeFalse();
    expect((component as any).loadPage).toHaveBeenCalledWith(0);
  });

  it('deve carregar destinos de transferencia excluindo o beneficio de origem', async () => {
    apiSpy.listar.and.returnValues(
      of(
        pageResponse(
          [
            { id: 1, nome: 'Origem', descricao: null, valor: 100, ativo: true, version: 0 },
            { id: 2, nome: 'Destino A', descricao: null, valor: 50, ativo: true, version: 0 }
          ],
          0,
          100,
          3,
          true
        )
      ),
      of(
        pageResponse(
          [{ id: 3, nome: 'Destino B', descricao: null, valor: 75, ativo: true, version: 0 }],
          1,
          100,
          3,
          false
        )
      )
    );

    await (component as any).loadTransferenciaDestinos(1);

    expect(apiSpy.listar).toHaveBeenCalledWith(0, 100);
    expect(apiSpy.listar).toHaveBeenCalledWith(1, 100);
    expect((component as any).transferenciaDestinos).toEqual([
      { id: 2, nome: 'Destino A' },
      { id: 3, nome: 'Destino B' }
    ]);
    expect((component as any).loadingTransferDestinos).toBeFalse();
  });

  it('deve manter pageSize atual quando resposta retornar size invalido', () => {
    const response = pageResponse([], 0, 0, 0, false);
    apiSpy.listar.and.returnValue(of(response));
    (component as any).pageSize = 4;

    (component as any).loadPage(0);

    expect((component as any).pageSize).toBe(4);
  });

  it('deve atualizar pageSize no onPageChange quando rows valido', () => {
    spyOn<any>(component, 'loadPage').and.stub();
    (component as any).pageSize = 4;

    (component as any).onPageChange({ page: 2, rows: 8 });

    expect((component as any).pageSize).toBe(8);
    expect((component as any).loadPage).toHaveBeenCalledWith(2);
  });

  it('deve preservar pageSize no onPageChange quando rows invalido', () => {
    spyOn<any>(component, 'loadPage').and.stub();
    (component as any).pageSize = 4;

    (component as any).onPageChange({ page: 1, rows: 0 });

    expect((component as any).pageSize).toBe(4);
    expect((component as any).loadPage).toHaveBeenCalledWith(1);
  });

  it('deve abrir dialogo de edicao com descricao vazia quando beneficio nao possui descricao', () => {
    (component as any).openEditDialog({
      id: 5,
      nome: 'Sem descricao',
      descricao: null,
      valor: 10,
      ativo: false,
      version: 1
    } as Beneficio);

    expect((component as any).editingId).toBe(5);
    expect((component as any).beneficioForm.getRawValue().descricao).toBe('');
    expect((component as any).beneficioDialogVisible).toBeTrue();
  });

  it('deve fechar dialogo de beneficio e resetar estado', () => {
    (component as any).editingId = 9;
    (component as any).beneficioDialogVisible = true;
    (component as any).beneficioForm.patchValue({
      nome: 'x',
      descricao: 'y',
      valor: 1,
      ativo: false
    });

    (component as any).closeBeneficioDialog();

    expect((component as any).beneficioDialogVisible).toBeFalse();
    expect((component as any).editingId).toBeNull();
    expect((component as any).beneficioForm.getRawValue()).toEqual({
      nome: '',
      descricao: '',
      valor: null,
      ativo: true
    });
  });

  it('nao deve salvar beneficio quando formulario invalido', () => {
    (component as any).openCreateDialog();
    (component as any).beneficioForm.patchValue({
      nome: '',
      descricao: '',
      valor: null,
      ativo: true
    });

    (component as any).saveBeneficio();

    expect(apiSpy.criar).not.toHaveBeenCalled();
    expect(apiSpy.atualizar).not.toHaveBeenCalled();
  });

  it('deve atualizar beneficio existente quando editingId estiver preenchido', () => {
    apiSpy.atualizar.and.returnValue(
      of({
        id: 1,
        nome: 'Atualizado',
        descricao: 'Descricao',
        valor: 33,
        ativo: false,
        version: 2
      })
    );
    spyOn<any>(component, 'loadPage').and.stub();
    (component as any).editingId = 1;
    (component as any).currentPage = 2;
    (component as any).beneficioForm.setValue({
      nome: ' Atualizado ',
      descricao: ' Descricao ',
      valor: 33,
      ativo: false
    });

    (component as any).saveBeneficio();

    expect(apiSpy.atualizar).toHaveBeenCalledWith(1, {
      nome: 'Atualizado',
      descricao: 'Descricao',
      valor: 33,
      ativo: false
    });
    expect((component as any).loadPage).toHaveBeenCalledWith(2);
  });

  it('deve notificar mensagem de erro da API ao salvar beneficio', () => {
    apiSpy.criar.and.returnValue(
      throwError(() => ({
        error: {
          message: 'Erro customizado'
        }
      }))
    );
    (component as any).openCreateDialog();
    (component as any).beneficioForm.setValue({
      nome: 'Novo',
      descricao: '',
      valor: 10,
      ativo: true
    });

    (component as any).saveBeneficio();

    const addArg = messageSpy.add.calls.mostRecent().args[0];
    expect(addArg.severity).toBe('error');
    expect(addArg.detail).toBe('Erro customizado');
  });

  it('deve usar mensagem padrao quando erro da API nao possui payload esperado', () => {
    apiSpy.criar.and.returnValue(throwError(() => new Error('erro inesperado')));
    (component as any).openCreateDialog();
    (component as any).beneficioForm.setValue({
      nome: 'Novo',
      descricao: '',
      valor: 10,
      ativo: true
    });

    (component as any).saveBeneficio();

    const addArg = messageSpy.add.calls.mostRecent().args[0];
    expect(addArg.severity).toBe('error');
    expect(addArg.detail).toBe('Falha ao salvar benefício.');
  });

  it('deve preparar e abrir dialogo de transferencia', () => {
    spyOn<any>(component, 'loadTransferenciaDestinos').and.returnValue(Promise.resolve());
    const origem: Beneficio = {
      id: 20,
      nome: 'Origem',
      descricao: null,
      valor: 300,
      ativo: true,
      version: 0
    };

    (component as any).openTransferDialog(origem);

    expect((component as any).transferenciaOrigem).toEqual(origem);
    expect((component as any).transferenciaDialogVisible).toBeTrue();
    expect((component as any).transferenciaForm.getRawValue()).toEqual({
      beneficioDestinoId: null,
      valor: null
    });
    expect((component as any).loadTransferenciaDestinos).toHaveBeenCalledWith(20);
  });

  it('deve fechar dialogo de transferencia e resetar estado', () => {
    (component as any).transferenciaDialogVisible = true;
    (component as any).transferenciaOrigem = {
      id: 1,
      nome: 'x',
      descricao: null,
      valor: 10,
      ativo: true,
      version: 0
    } as Beneficio;
    (component as any).transferenciaDestinos = [{ id: 2, nome: 'Destino' }];
    (component as any).transferenciaForm.patchValue({
      beneficioDestinoId: 2,
      valor: 5
    });

    (component as any).closeTransferDialog();

    expect((component as any).transferenciaDialogVisible).toBeFalse();
    expect((component as any).transferenciaOrigem).toBeNull();
    expect((component as any).transferenciaDestinos).toEqual([]);
    expect((component as any).transferenciaForm.getRawValue()).toEqual({
      beneficioDestinoId: null,
      valor: null
    });
  });

  it('nao deve transferir quando formulario estiver invalido', () => {
    (component as any).transferenciaOrigem = {
      id: 1,
      nome: 'Origem',
      descricao: null,
      valor: 20,
      ativo: true,
      version: 0
    } as Beneficio;
    (component as any).transferenciaForm.setValue({
      beneficioDestinoId: null,
      valor: null
    });

    (component as any).transferir();

    expect(apiSpy.transferir).not.toHaveBeenCalled();
  });

  it('deve notificar erro da API ao falhar transferencia', () => {
    apiSpy.transferir.and.returnValue(
      throwError(() => ({
        error: {
          message: 'Transferencia negada'
        }
      }))
    );
    (component as any).transferenciaOrigem = {
      id: 1,
      nome: 'Origem',
      descricao: null,
      valor: 100,
      ativo: true,
      version: 0
    } as Beneficio;
    (component as any).transferenciaForm.setValue({
      beneficioDestinoId: 2,
      valor: 50
    });

    (component as any).transferir();

    const addArg = messageSpy.add.calls.mostRecent().args[0];
    expect(addArg.severity).toBe('error');
    expect(addArg.detail).toBe('Transferencia negada');
  });

  it('deve notificar erro ao falhar carregamento de destinos da transferencia', async () => {
    apiSpy.listar.and.returnValue(throwError(() => new Error('falhou')));

    await (component as any).loadTransferenciaDestinos(1);

    const addArg = messageSpy.add.calls.mostRecent().args[0];
    expect(addArg.severity).toBe('error');
    expect(addArg.detail).toBe('Falha ao carregar benefícios para destino da transferência.');
    expect((component as any).loadingTransferDestinos).toBeFalse();
  });

  it('deve avaliar severidade de status corretamente', () => {
    expect((component as any).statusSeverity(true)).toBe('success');
    expect((component as any).statusSeverity(false)).toBe('danger');
  });

  it('deve avaliar fieldInvalid para os dois formularios', () => {
    const beneficioNome = (component as any).beneficioForm.get('nome');
    beneficioNome?.markAsTouched();
    beneficioNome?.setValue('');
    expect((component as any).fieldInvalid('beneficio', 'nome')).toBeTrue();

    (component as any).transferenciaForm.get('beneficioDestinoId')?.markAsTouched();
    (component as any).transferenciaForm.get('beneficioDestinoId')?.setValue(null);
    expect((component as any).fieldInvalid('transferencia', 'beneficioDestinoId')).toBeTrue();

    (component as any).transferenciaForm.get('beneficioDestinoId')?.setValue(2);
    expect((component as any).fieldInvalid('transferencia', 'beneficioDestinoId')).toBeFalse();
  });
});
