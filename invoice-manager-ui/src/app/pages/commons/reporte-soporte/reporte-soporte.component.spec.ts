import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReporteSoporteComponent } from './reporte-soporte.component';

describe('ReporteSoporteComponent', () => {
  let component: ReporteSoporteComponent;
  let fixture: ComponentFixture<ReporteSoporteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReporteSoporteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReporteSoporteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
