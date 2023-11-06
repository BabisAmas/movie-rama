export class Movie {
    id!: number;
    title!: string;
    description!: string;
    dateAdded!: Date;
    numberOfLikes!: number;
    numberOfHates!: number;
    loggedUserVote!: string;
    loggedUserAuthor!: boolean;
}