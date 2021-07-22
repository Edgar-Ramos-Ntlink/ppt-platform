import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ObservacionPendientesComponent } from './observacion-pendientes.component';

describe('ObservacionPendientesComponent', () => {
  let component: ObservacionPendientesComponent;
  let fixture: ComponentFixture<ObservacionPendientesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ObservacionPendientesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ObservacionPendientesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
