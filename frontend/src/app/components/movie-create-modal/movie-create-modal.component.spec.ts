import { ComponentFixture, TestBed } from '@angular/core/testing';

import { MovieCreateModalComponent } from './movie-create-modal.component';

describe('MovieCreateModalComponent', () => {
  let component: MovieCreateModalComponent;
  let fixture: ComponentFixture<MovieCreateModalComponent>;

  beforeEach(() => {
    TestBed.configureTestingModule({
      declarations: [MovieCreateModalComponent]
    });
    fixture = TestBed.createComponent(MovieCreateModalComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
