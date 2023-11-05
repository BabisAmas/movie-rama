import { Component, EventEmitter, Output } from '@angular/core';
import { SelectItem } from 'primeng/api';

@Component({
  selector: 'app-sort',
  templateUrl: './sort.component.html',
  styleUrls: ['./sort.component.scss'],
})
export class SortComponent {
  @Output() onSort = new EventEmitter<string>();

  sortOptions: SelectItem[];

  constructor() {
    this.sortOptions = [
      { label: 'Newest First', value: 'addedDateDesc' },
      { label: 'Oldest First', value: 'addedDateAsc' },
      { label: 'Most Liked', value: 'mostLikes' },
      { label: 'Most Hated', value: 'mostHates' },
    ];
  }

  onSortChange(event: any): void {
    this.onSort.emit(event.value);
  }
}
