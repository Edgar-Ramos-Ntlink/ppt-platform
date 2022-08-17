import { ComponentFixture, TestBed } from '@angular/core/testing';

import { SeleccionPagosComponent } from './seleccion-pagos.component';

describe('SeleccionPagosComponent', () => {
  let component: SeleccionPagosComponent;
  let fixture: ComponentFixture<SeleccionPagosComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ SeleccionPagosComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SeleccionPagosComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
