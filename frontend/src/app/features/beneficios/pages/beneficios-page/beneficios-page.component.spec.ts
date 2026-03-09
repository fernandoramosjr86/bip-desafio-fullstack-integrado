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
});
