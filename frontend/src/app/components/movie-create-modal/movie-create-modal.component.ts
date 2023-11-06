import { Component, EventEmitter, Input, Output } from '@angular/core';
import { AbstractControl, FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { MessageService } from 'primeng/api';
import { MovieService } from 'src/app/services/movie.service';

@Component({
  selector: 'app-movie-create-modal',
  templateUrl: './movie-create-modal.component.html',
  styleUrls: ['./movie-create-modal.component.scss']
})
export class MovieCreateModalComponent {
  @Input() displayMovieCreate: boolean = false;
  @Output() displayMovieCreateChange = new EventEmitter<boolean>();
  @Output() close = new EventEmitter<void>();
  @Output() movieCreated = new EventEmitter<void>();

  movieForm!: FormGroup;

  constructor(private movieService: MovieService, private formBuilder: FormBuilder, private messageService: MessageService) {
    this.createForm();
  }

  private createForm() {
    this.movieForm = this.formBuilder.group({
      title: ['', [Validators.required, Validators.minLength(1), Validators.maxLength(255)]],
      description: ['', [Validators.maxLength(1000)]]
    });
  }

  getFormControl(controlName: string): AbstractControl | null {
    return this.movieForm.get(controlName);
  }
  
  hasError(controlName: string, errorName: string): boolean {
    const control = this.getFormControl(controlName);
    return control?.touched && control?.hasError(errorName) || false;
  }
  onCreateMovie() {
    if (this.movieForm.valid) {
      this.movieService.createMovie(this.movieForm.value).subscribe({
        next: (newMovie) => {
          this.messageService.add({
            severity: 'success',
            summary: 'Success',
            detail: `Movie "${newMovie.title}" was created successfully.`
          });
          this.displayMovieCreate = false;
          this.movieCreated.emit();
          this.displayMovieCreateChange.emit(this.displayMovieCreate);
          this.close.emit();
        },
        error: (error) => {
          this.messageService.add({
            severity: 'error',
            summary: 'Error',
            detail: `Failed to create the movie. Error: ${error.message}`
          });
        }
      });
    }
  }

   modalClose() {
    this.displayMovieCreateChange.emit(false);
    this.close.emit();
  }
}
