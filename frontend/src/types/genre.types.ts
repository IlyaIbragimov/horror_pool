import type { PaginatedResponse } from "./pagination.types";

export type Genre = {
    genreId: number;
    name: string;
    description: string | null;
    posterPath: string | null;
}

export type GenreOption = {
    genreId: number;
    name: string;
}

export type GenreAllResponse = PaginatedResponse & {
    genres: Genre[];
};
