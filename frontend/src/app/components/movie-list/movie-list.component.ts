import { Component, HostListener } from '@angular/core';
import { MovieService } from 'src/app/services/movie.service';

@Component({
  selector: 'app-movie-list',
  templateUrl: './movie-list.component.html',
  styleUrls: ['./movie-list.component.scss'],
})
export class MovieListComponent {
  movies: any[] = [];
  sortKey: string = 'addedDateDesc';
  page: number = 0;
  size: number = 10;
  totalElements: number = 0;
  notEmptyPost = true;
  notscrolly = true;
  private initialSortDone = false;

  constructor(private movieService: MovieService) {}

  ngOnInit(): void {
    this.loadInitMovies();
    this.initialSortDone = true;
  }

  loadInitMovies(): void {
    this.movieService
      .listMovies(this.page, this.size, this.sortKey)
      .subscribe((data) => {
        this.movies = data.content;
        this.totalElements = data.totalElements;
      });
  }

  onSortChange(event: any): void {
    if (this.initialSortDone) {
      this.sortKey = event.value;
      this.page = 0;
      this.notEmptyPost = true;
      this.notscrolly = true;
      this.loadInitMovies();
    } else {
      this.initialSortDone = true;
    }
  }

  @HostListener('window:scroll', [])
  onScroll(): void {
    if (window.innerHeight + window.scrollY >= document.body.offsetHeight) {
      this.loadMoreMovies();
    }
  }

  loadMoreMovies(): void {
    if (this.notscrolly && this.notEmptyPost) {
      this.page++;
      this.notscrolly = false;
      this.movieService
        .listMovies(this.page, this.size, this.sortKey)
        .subscribe((data) => {
          const newMovies = data.content;
          this.totalElements = data.totalElements;

          if (newMovies.length === 0) {
            this.notEmptyPost = false;
          }

          this.movies = this.movies.concat(newMovies);
          this.notscrolly = true;
        });
    }
  }
}