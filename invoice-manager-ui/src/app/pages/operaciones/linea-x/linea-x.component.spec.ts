import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { LineaXComponent } from './linea-x.component';

describe('LineaXComponent', () => {
  let component: LineaXComponent;
  let fixture: ComponentFixture<LineaXComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ LineaXComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(LineaXComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
