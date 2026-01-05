export type Genre = {
    genreId: number;
    name: string;
    description: string | null;
    posterPath: string | null;
}

export type GenreAllResponse = {
    genres: Genre[];
    pageNumber: number;
    pageSize: number;
    totalElements?: number;
    totalPages: number;
    lastPage: boolean
}