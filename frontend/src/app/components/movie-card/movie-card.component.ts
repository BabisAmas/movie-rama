import { Component, Input, OnInit } from '@angular/core';
import { MessageService } from 'primeng/api';
import { AuthService } from 'src/app/services/auth.service';
import { MovieService } from 'src/app/services/movie.service';
import { VoteService } from 'src/app/services/vote.service';

@Component({
  selector: 'app-movie-card',
  templateUrl: './movie-card.component.html',
  styleUrls: ['./movie-card.component.scss'],
})
export class MovieCardComponent implements OnInit {
  @Input() movie: any;

  constructor(
    private voteService: VoteService,
    private authService: AuthService,
    private messageService: MessageService
  ) {}

  ngOnInit(): void {}

  likeMovie(movieId: number): void {
    if (this.movie.loggedUserAuthor) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Action Forbidden',
        detail: 'You cannot like your own movie.'
      });
      return;
    }

    if (!this.authService.isLoggedIn()) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Authentication Required',
        detail: 'You need to be logged in to like a movie.',
      });
      return;
    }

    const previousVote = this.movie.loggedUserVote;
    this.voteService.vote(movieId, 'LIKE').subscribe(() => {
      if (previousVote === 'HATE') {
        this.movie.numberOfHates--;
      }
      if (previousVote !== 'LIKE') {
        this.movie.numberOfLikes++;
      }
      this.movie.loggedUserVote = 'LIKE';
    });
  }

  dislikeMovie(movieId: number): void {
    if (this.movie.loggedUserAuthor) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Action Forbidden',
        detail: 'You cannot dislike your own movie.'
      });
      return;
    }
    
    if (!this.authService.isLoggedIn()) {
      this.messageService.add({
        severity: 'warn',
        summary: 'Authentication Required',
        detail: 'You need to be logged in to dislike a movie.',
      });
      return;
    }

    const previousVote = this.movie.loggedUserVote;
    this.voteService.vote(movieId, 'HATE').subscribe(() => {
      if (previousVote === 'LIKE') {
        this.movie.numberOfLikes--;
      }
      if (previousVote !== 'HATE') {
        this.movie.numberOfHates++;
      }
      this.movie.loggedUserVote = 'HATE';
    });
  }

  removeVote(movieId: number): void {
    if (!this.authService.isLoggedIn()) {
      return;
    }
    this.voteService.removeVote(movieId).subscribe(() => {
      if (this.movie.loggedUserVote === 'LIKE') this.movie.numberOfLikes--;
      else if (this.movie.loggedUserVote === 'HATE') this.movie.numberOfHates--;
      this.movie.loggedUserVote = null;
    });
  }

  hasVoted(voteType: string): boolean {
    return this.movie.loggedUserVote === voteType;
  }
}