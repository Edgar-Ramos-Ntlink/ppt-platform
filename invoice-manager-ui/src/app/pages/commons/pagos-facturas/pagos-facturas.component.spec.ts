import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PagosFacturasComponent } from './pagos-facturas.component';

describe('PagosFacturasComponent', () => {
  let component: PagosFacturasComponent;
  let fixture: ComponentFixture<PagosFacturasComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ PagosFacturasComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PagosFacturasComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
