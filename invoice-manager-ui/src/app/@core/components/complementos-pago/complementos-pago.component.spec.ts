import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ComplementosPagoComponent } from './complementos-pago.component';

describe('ComplementosPagoComponent', () => {
  let component: ComplementosPagoComponent;
  let fixture: ComponentFixture<ComplementosPagoComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [ ComplementosPagoComponent ]
    })
    .compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(ComplementosPagoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
