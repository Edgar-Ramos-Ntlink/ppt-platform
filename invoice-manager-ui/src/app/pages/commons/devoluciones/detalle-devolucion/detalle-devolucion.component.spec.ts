import { ComponentFixture, TestBed } from '@angular/core/testing';

import { DetalleDevolucionComponent } from './detalle-devolucion.component';

describe('DetalleDevolucionComponent', () => {
  let component: DetalleDevolucionComponent;
  let fixture: ComponentFixture<DetalleDevolucionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ DetalleDevolucionComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(DetalleDevolucionComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
